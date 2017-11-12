package edu.miami.cse.reversi.strategy;

import java.util.HashMap;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class AI implements Strategy{
	
	@Override
	public Square chooseSquare(Board board) {
		float v = Integer.MIN_VALUE;
		Square best = board.getCurrentPossibleSquares().iterator().next();
		for(Square s:board.getCurrentPossibleSquares()) {
			float a = alphaBetaPrunning(board.play(s), 3, Float.MIN_VALUE, Float.MAX_VALUE, false);
			if(a > v) {
				v = a;
				best = s;
			}
		}
		return best;
	}
	public float alphaBetaPrunning(Board board, int depth, float alpha, float beta, boolean max) {
		if(depth == 0) {
			Player p = board.getCurrentPlayer().opponent();
			int cornerSquare = 0;
			if(board.getSquareOwners().get(new Square(0,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(0,  7)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  7)) == p) cornerSquare++;
			return board.getPlayerSquareCounts().get(p) * 0.01f + board.getCurrentPossibleSquares().size() + cornerSquare * 10;
		}
		
		if(max) {
			float v = Integer.MIN_VALUE;
			for(Square s:board.getCurrentPossibleSquares()) {
				v = Math.max(v, alphaBetaPrunning(board.play(s), depth-1, alpha, beta, !max));
				alpha = Math.max(alpha, v);
				// need a ways to undo board.play(s)
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
			for(Square s:board.getCurrentPossibleSquares()) {
				v = Math.min(v, alphaBetaPrunning(board.play(s), depth-1, alpha, beta, !max));
				beta = Math.min(beta, v);
				//need a ways to undo board.play(s)
			}
			Player p = board.getCurrentPlayer();
			if(board.isComplete()) {
				if(board.getWinner() == p) return Integer.MAX_VALUE;
				else return Integer.MIN_VALUE;
			}
			return v;
		}
 	}
}
