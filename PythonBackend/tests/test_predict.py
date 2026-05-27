from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_predict_invalid_learner():

    payload = {
        "learner_id": 999,
        "gpa": 8.5,
        "skills": "Python,ML,FastAPI",
        "experience_months": 6,
        "course": "Computer Science",
        "semester": 8
    }

    response = client.post(
        "/predict/999",
        json=payload
    )

    assert response.status_code == 404


def test_bulk_prediction():

    payload = {
        "learner_ids": [1, 2, 3]
    }

    response = client.post(
        "/predict/bulk",
        json=payload
    )

    assert response.status_code == 200

    data = response.json()

    assert "predictions" in data
    assert "total" in data