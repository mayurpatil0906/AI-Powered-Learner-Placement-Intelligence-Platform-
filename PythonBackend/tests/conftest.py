import pytest
from fastapi.testclient import TestClient

from app.main import app
from app.database import get_db


class MockQuery:

    def filter(self, *args, **kwargs):
        return self

    def order_by(self, *args, **kwargs):
        return self

    def group_by(self, *args, **kwargs):
        return self

    def limit(self, *args, **kwargs):
        return self

    def first(self):
        return None

    def all(self):
        return []


class MockDB:

    def query(self, *args, **kwargs):
        return MockQuery()

    def add(self, obj):
        pass

    def commit(self):
        pass

    def refresh(self, obj):
        pass

    def close(self):
        pass


def override_get_db():
    yield MockDB()


app.dependency_overrides[get_db] = override_get_db


@pytest.fixture
def client():
    return TestClient(app)