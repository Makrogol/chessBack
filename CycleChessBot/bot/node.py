import config
import math
import logging

from chess_game.move import Move
from chess_game.color import Color
from chess_game.game_state import is_game_over
from chess_game.move_type import MoveType
from chess_game.cpp_api import CppApi

logging.basicConfig(level=logging.INFO, filename="_log_.log", format=' %(message)s')


class Edge:
    def __init__(self, input_node: "Node", output_node: "Node", move: Move, prior: float):
        self.input_node = input_node
        self.output_node = output_node
        self.move = move

        # self.player_turn = self.input_node.cpp_api.getCurrentTurn()

        # each move stores 4 numbers:
        self.N = 0  # amount of times this move has been taken (=visit count)
        self.W = 0  # total move-value
        self.P = prior  # prior probability of selecting this move

    def __eq__(self, edge: object) -> bool:
        if isinstance(edge, Edge):
            return self.move == edge.move and self.input_node.fen == edge.input_node.fen
        else:
            return NotImplemented

    def __str__(self):
        return f"{self.move}: Q={self.W / self.N if self.N != 0 else 0}, N={self.N}, W={self.W}, P={self.P}, U = {self.upper_confidence_bound()}"

    def __repr__(self):
        return f"{self.move}: Q={self.W / self.N if self.N != 0 else 0}, N={self.N}, W={self.W}, P={self.P}, U = {self.upper_confidence_bound()}"

    def upper_confidence_bound(self, noise: float) -> float:
        exploration_rate = math.log((1 + self.input_node.N + config.C_base) / config.C_base) + config.C_init
        ucb = exploration_rate * (self.P * noise) * (math.sqrt(self.input_node.N) / (1 + self.N))
        if self.input_node.turn == Color.WHITE:
            return self.W / (self.N + 1) + ucb 
        else:
            return -(self.W / (self.N + 1)) + ucb

class Node:
    def __init__(self, fen: str = config.DEFAULT_FEN):
        """
        A node is a fen inside the MCTS tree.
        """
        self.fen = fen
        self.cpp_api = CppApi()
        if self.cpp_api.getFen() != fen:
            self.cpp_api.startGameWithFen(Color.WHITE, fen)
        self.turn = self.cpp_api.getCurrentTurn()
        # the edges connected to this node
        self.edges: list[Edge] = []
        # the visit count for this node
        self.N = 0

        self.value = 0

    def __eq__(self, node: object) -> bool:
        """
        Check if two nodes are equal.
        Two nodes are equal if the fen is the same
        """
        if isinstance(node, Node):
            return self.fen == node.fen
        else:
            return NotImplemented

    def step(self, move: Move) -> str:
        """
        Take a step in the game, returns new state
        """
        if self.cpp_api.getFen() != self.fen:
            self.cpp_api.startGameWithFen(Color.WHITE, self.fen)
        if self.cpp_api.tryDoMove(move) != MoveType.NOT_MOVE:
            return self.cpp_api.getFen()
        else:
            logging.info(f"<node> can't do the move: {move} from node fen: {self.fen}")

    def is_game_over(self) -> bool:
        """
        Check if the game is over.
        """
        if self.cpp_api.getFen() != self.fen:
            self.cpp_api.startGameWithFen(Color.WHITE, self.fen)
        return is_game_over(self.cpp_api.getGameState())

    def is_leaf(self) -> bool:
        """
        Check if the current node is a leaf node.
        """
        return self.N == 0

    def add_child(self, child, move: Move, prior: float) -> Edge:
        """
        Add a child node to the current node.

        Returns the created edge between the nodes
        """
        edge = Edge(input_node=self, output_node=child, move=move, prior=prior)
        self.edges.append(edge)
        return edge

    def get_all_children(self):
        """
        Get all children of the current node and their children, recursively
        """
        children = []
        for edge in self.edges:
            children.append(edge.output_node)
            children.extend(edge.output_node.get_all_children())
        return children

    def get_edge(self, move) -> Edge:
        """
        Get the edge between the current node and the child node with the given move.
        """
        for edge in self.edges:
            if edge.move == move:
                return edge
        return None
