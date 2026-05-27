from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from datetime import datetime

from app.database import get_db
from app.models import Learner, MLPrediction
from app.schemas import (
    LearnerInput,
    PredictionResponse,
    PredictionHistoryItem,
    ModelStatusResponse,
    RetrainResponse,
    BulkPredictionRequest,
    BulkPredictionResponse,
)
from app.ml_service import ml_service
from app.logger import logger

router = APIRouter()


# ---------------- BULK ROUTE FIRST ----------------

@router.post("/predict/bulk", response_model=BulkPredictionResponse)
async def predict_bulk(request: BulkPredictionRequest, db: Session = Depends(get_db)):

    logger.info(f"Bulk prediction requested for {len(request.learner_ids)} learners")

    predictions = []

    for learner_id in request.learner_ids:

        learner = db.query(Learner).filter(Learner.id == learner_id).first()

        if not learner:
            continue

        probability, placeable, top_factors = ml_service.predict(
            gpa=learner.gpa or 0.0,
            experience_months=learner.experience_months or 0,
            skills=learner.skills or "",
            semester=learner.semester or 1,
            course=learner.course or "",
        )

        pred = MLPrediction(
            learner_id=learner_id,
            placement_probability=probability,
            placeable=placeable,
            top_factors=",".join(top_factors),
            features_used=f"gpa={learner.gpa}",
            predicted_at=datetime.utcnow(),
        )

        db.add(pred)

        predictions.append(
            PredictionResponse(
                learner_id=learner_id,
                learner_name=learner.name,
                probability=probability,
                placeable=placeable,
                top_factors=top_factors,
                predicted_at=pred.predicted_at,
            )
        )

    db.commit()

    return BulkPredictionResponse(
        predictions=predictions,
        total=len(predictions)
    )


# ---------------- SINGLE PREDICTION ROUTE ----------------

@router.post("/predict/{learner_id}", response_model=PredictionResponse)
async def predict_placement(
    learner_id: int,
    data: LearnerInput,
    db: Session = Depends(get_db)
):

    logger.info(f"Prediction requested for learner_id={learner_id}")

    learner = db.query(Learner).filter(Learner.id == learner_id).first()

    if not learner:
        raise HTTPException(
            status_code=404,
            detail=f"Learner not found: {learner_id}"
        )

    probability, placeable, top_factors = ml_service.predict(
        gpa=data.gpa,
        experience_months=data.experience_months,
        skills=data.skills,
        semester=data.semester,
        course=data.course,
    )

    prediction = MLPrediction(
        learner_id=learner_id,
        placement_probability=probability,
        placeable=placeable,
        top_factors=",".join(top_factors),
        features_used=str(data.model_dump()),
        predicted_at=datetime.utcnow(),
    )

    db.add(prediction)
    db.commit()
    db.refresh(prediction)

    return PredictionResponse(
        learner_id=learner_id,
        learner_name=learner.name,
        probability=probability,
        placeable=placeable,
        top_factors=top_factors,
        predicted_at=prediction.predicted_at,
    )