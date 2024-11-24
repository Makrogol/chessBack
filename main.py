from fastapi import FastAPI

app = FastAPI()

@app.get("/app")
def hello_world():
    return {
        "message": "Hello World!"
    }
