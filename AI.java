package edu.miami.cse.reversi.strategy;

import java.util.HashMap;
import java.util.PriorityQueue;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class AI implements Strategy{
	public static long time = 0, move = 0;
	@Override
	public Square chooseSquare(Board board) {
		double v = Double.MIN_VALUE;
		Square best = board.getCurrentPossibleSquares().iterator().next();
		long t = System.currentTimeMillis();
		for(Square s:board.getCurrentPossibleSquares()) {
			double a = alphaBetaPrunning(board.play(s), 3, Double.MIN_VALUE, Double.MAX_VALUE, board.getCurrentPlayer());
			if(a > v) {
				v = a;
				best = s;
			}
		}
		time += System.currentTimeMillis() - t;
		move++;
		return best;
	}
	public double alphaBetaPrunning(Board board, int depth, double alpha, double beta, Player maxPlayer) {

		if(depth == 0) {
			Player p = maxPlayer;
			int diff = board.getPlayerSquareCounts().get(p) - board.getPlayerSquareCounts().get(p.opponent());
			int moveMade = board.getPlayerSquareCounts().get(p) + board.getPlayerSquareCounts().get(p.opponent());
			int cornerSquare = 0;
			if(board.getSquareOwners().get(new Square(0,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(0,  7)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  0)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(7,  7)) == p) cornerSquare++;
			if(board.getSquareOwners().get(new Square(0,  0)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(0,  7)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(7,  0)) == p.opponent()) cornerSquare--;
			if(board.getSquareOwners().get(new Square(7,  7)) == p.opponent()) cornerSquare--;
			return (double)(diff * 5 * (Math.pow(Math.E, -moveMade))) + board.getCurrentPossibleSquares().size() + cornerSquare * 10;
		}
		
		if (board.getCurrentPlayer() == maxPlayer) {
			// Maximizing Player
			double value = Double.MIN_VALUE;
			for (Square square : board.getCurrentPossibleSquares()) {
				Board nextBoard = board.play(square);
				value = Math.max(value, alphaBetaPrunning(nextBoard, depth - 1, alpha, beta, maxPlayer));
				alpha = Math.max(alpha, value);
				if (beta <= alpha) {
					// Beta cutoff
					break;
				}
			}

			return value;
		} else {
			// Minimizing Player
			double value = Double.MAX_VALUE;
			for (Square square : board.getCurrentPossibleSquares()) {
				Board nextBoard = board.play(square);
				value = Math.min(value, alphaBetaPrunning(nextBoard, depth - 1, alpha, beta, maxPlayer));
				beta = Math.min(beta, value);
				if (beta <= alpha) {
					// Alpha cutoff
					break;
				}
			}

			return value;
		}
 	}
	
	public PriorityQueue<Board> OrderHeuristic(Board board) {
		PriorityQueue<Board> heap = new PriorityQueue<>((Board a, Board b) -> cornerPoint(a) - cornerPoint(b) + a.getCurrentPossibleSquares().size()-b.getCurrentPossibleSquares().size());
		for(Square s:board.getCurrentPossibleSquares()) {
			heap.add(board.play(s));
		}
		return heap;
	}
	
	public int cornerPoint(Board board) {
		Player p = board.getCurrentPlayer().opponent();
		int cornerSquare = 0;
		if(board.getSquareOwners().get(new Square(0,  0)) == p) cornerSquare++;
		if(board.getSquareOwners().get(new Square(0,  7)) == p) cornerSquare++;
		if(board.getSquareOwners().get(new Square(7,  0)) == p) cornerSquare++;
		if(board.getSquareOwners().get(new Square(7,  7)) == p) cornerSquare++;
		if(board.getSquareOwners().get(new Square(0,  0)) == p.opponent()) cornerSquare--;
		if(board.getSquareOwners().get(new Square(0,  7)) == p.opponent()) cornerSquare--;
		if(board.getSquareOwners().get(new Square(7,  0)) == p.opponent()) cornerSquare--;
		if(board.getSquareOwners().get(new Square(7,  7)) == p.opponent()) cornerSquare--;
		return cornerSquare*10;
	}
}
