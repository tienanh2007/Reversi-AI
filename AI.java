package edu.miami.cse.reversi.strategy;

import java.util.HashMap;
import java.util.PriorityQueue;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class AI implements Strategy{
	private static final int EPSILON = 5;
	@Override
	public Square chooseSquare(Board board) {
		float v = Integer.MIN_VALUE;
		Square best = board.getCurrentPossibleSquares().iterator().next();
		for(Square s:board.getCurrentPossibleSquares()) {
			float a = alphaBetaPrunning(board.play(s), 2, Float.MIN_VALUE, Float.MAX_VALUE, false);
			if(a > v) {
				v = a;
				best = s;
			}
		}
		return best;
	}
	public float alphaBetaPrunning(Board board, int depth, float alpha, float beta, boolean max) {
		if(depth == 0) {
			Player p = max ? board.getCurrentPlayer() : board.getCurrentPlayer().opponent();
			int cornerSquare = 0;
			if(board.getSquareOwners().get(new Square(0,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(0,  7)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  7)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(0,  0)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(0,  7)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(7,  0)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(7,  7)) == p.opponent()) cornerSquare--;
			return board.getPlayerSquareCounts().get(p) * 0.01f + board.getCurrentPossibleSquares().size() + cornerSquare * 10;
		}
		
		if(max) {
			float v = Integer.MIN_VALUE;
			for(Square s:board.getCurrentPossibleSquares()) {
				v = Math.max(v, alphaBetaPrunning(board.play(s), depth-1, alpha, beta, !max));
				alpha = Math.max(alpha, v);
				if (beta <= alpha) break;
			}
			Player p = board.getCurrentPlayer();
			if(board.isComplete()) {
				if(board.getWinner() == p) return Integer.MAX_VALUE;
				else return Integer.MIN_VALUE;
			}
			return v;
		} else {
			float v = Integer.MAX_VALUE;
			PriorityQueue<Board> heap = OrderHeuristic(board);
			while(!heap.isEmpty()) {
				v = Math.min(v, alphaBetaPrunning(heap.poll(), depth-1, alpha, beta, !max));
				beta = Math.min(beta, v);
				if (beta <= alpha) break;
			}
			Player p = board.getCurrentPlayer();
			if(board.isComplete()) {
				if(board.getWinner() == p) return Integer.MAX_VALUE;
				else return Integer.MIN_VALUE;
			}
			return v;
		}
 	}
	
	public PriorityQueue<Board> OrderHeuristic(Board board) {
		PriorityQueue<Board> heap = new PriorityQueue<>((Board a, Board b) -> a.getCurrentPossibleSquares().size()-b.getCurrentPossibleSquares().size());
		for(Square s:board.getCurrentPossibleSquares()) {
			heap.add(board.play(s));
		}
		return heap;
	}
}
