from fastapi import APIRouter

from .auth.views import router as auth_router
from .game.views import router as game_router
from .game.web_socket_manager import WebSocketManager

router = APIRouter()
router.include_router(router=auth_router)
router.include_router(router=game_router)
