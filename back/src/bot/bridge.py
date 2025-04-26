from typing import Tuple

import numpy as np

from . import config
from .agent import Agent
from ..cpp_bridge.move import Move


class Bridge:
    def __init__(self, model_path: str):
        self.model_path = model_path

    def predict_move(self, fen: str) -> Tuple[Move, str]:
        print(f"predict move for fen {fen}")
        agent = Agent(fen=fen, local_predictions=True, model_path=self.model_path)
        print("predict move create agent")
        agent.run_simulations(n=config.SIMULATIONS_PER_MOVE)
        moves = agent.mcts.root.edges
        print(f"predict move find all moves")

        sum_move_visits = sum(e.N for e in moves)
        probs = [e.N / sum_move_visits for e in moves]

        best_move = moves[np.argmax(probs)]
        print(f"predict move find best move: {best_move}")

        return best_move.move, best_move.output_node.fen
