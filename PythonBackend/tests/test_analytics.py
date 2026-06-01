from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_top_learners():
    response = client.get("/analytics/top-learners")

    assert response.status_code == 200

    assert isinstance(response.json(), list)


def test_weak_learners():
    response = client.get("/analytics/weak-learners")

    assert response.status_code == 200

    assert isinstance(response.json(), list)


def test_batch_performance():
    response = client.get("/analytics/batch-performance")

    assert response.status_code == 200

    assert isinstance(response.json(), list)


def test_placement_trends():
    response = client.get("/analytics/placement-trends")

    assert response.status_code == 200

    data = response.json()

    assert "total_learners" in data
    assert "placement_ready" in data
    assert "moderate_risk" in data
    assert "high_risk" in data