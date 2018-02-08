package com.pyrsoftware;

/**
 * The class represents a single card object
 * @author Quan Meng
 *
 */
public class Card {
	public static int BELONGS_TO_HAND = 0;
	public static int BELONGS_TO_BOARD = 1;
	
	private String num;
	private int numeric;
	private String suit;
	private int belongsTo;
	
	public int getNumeric() {
		return numeric;
	}
	public void setNumeric(int numeric) {
		this.numeric = numeric;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String cardNum) {
		this.num = cardNum;
	}
	public String getSuit() {
		return suit;
	}
	public void setSuit(String suit) {
		this.suit = suit;
	}
	public int getBelongsTo() {
		return belongsTo;
	}
	public void setBelongsTo(int belongsTo) {
		this.belongsTo = belongsTo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numeric;
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (numeric != other.numeric)
			return false;
		if (suit == null) {
			if (other.suit != null)
				return false;
		} else if (!suit.equals(other.suit))
			return false;
		return true;
	}
}
