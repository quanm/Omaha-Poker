package com.pyrsoftware;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class combines one hand cards with the board cards and calculate the highest 
 * rank cards for that hand
 * @author Quan Meng
 *
 */
public class OneCombination {
	public static final int HAND_TYPE_HIGH_CARD			= 1;
	public static final int HAND_TYPE_ONE_PAIR			= 2;
	public static final int HAND_TYPE_TWO_PAIR			= 3;
	public static final int HAND_TYPE_3_OF_A_KIND		= 4;
	public static final int HAND_TYPE_STRAIGHT			= 5;
	public static final int HAND_TYPE_FLUSH				= 6;
	public static final int HAND_TYPE_FULL_HOUSE		= 7;
	public static final int HAND_TYPE_4_OF_A_KIND		= 8;
	public static final int HAND_TYPE_STRAIGHT_FLUSH	= 9;
	
	public static final int NUM_TOTAL_CARDS		= 5;
	public static final int NUM_HAND_CARDS		= 2;
	public static final int NUM_BOARD_CARDS		= 3;
	
	private List<List<Card>> comboList 		= new ArrayList<List<Card>>();
	private List<Card> diamondList 			= new ArrayList<Card>();
	private List<Card> clubList 			= new ArrayList<Card>();
	private List<Card> heartList 			= new ArrayList<Card>();
	private List<Card> spadeList 			= new ArrayList<Card>();
	
	private List<Card> highRankCardList 	= new ArrayList<Card>();
	private int highRankHandType;
	
	public OneCombination(String hand, String board){
		// Init the comboList with 14 arrayList (As 'A' needs to be added both beginning and end)
		for(int i=0; i<14; i++){
			this.getComboList().add(new ArrayList<Card>());
		}
		this.constructAndFillCards(hand, true);
		this.constructAndFillCards(board, false);
	}
	
	/**
	 * Construct the card and fill the card into the combo list and corresponding suit list
	 * @param cardChain
	 * @param belongsToHand
	 */
	private void constructAndFillCards(String cardChain, boolean belongsToHand){
		String[] cardStrSplit = cardChain.split("-");
		for(String cardStr : cardStrSplit){
			char num = cardStr.charAt(0);
			char suit = cardStr.charAt(1);
			Card card = new Card();
			card.setNum(Character.toString(num));
			card.setSuit(Character.toString(suit));
			card.setBelongsTo(belongsToHand?Card.BELONGS_TO_HAND:Card.BELONGS_TO_BOARD);
			// Fill in the comboList list for each card
			switch(num){
				case 'A':
					// For 'A', need to fill in the first and also the last space
					card.setNumeric(14);
					this.getComboList().get(0).add(card);
					this.getComboList().get(13).add(card);
					break;
				case 'T':
					card.setNumeric(10);
					this.getComboList().get(9).add(card);
					break;				
				case 'J':
					card.setNumeric(11);
					this.getComboList().get(10).add(card);
					break;
				case 'Q':
					card.setNumeric(12);
					this.getComboList().get(11).add(card);
					break;
				case 'K':
					card.setNumeric(13);
					this.getComboList().get(12).add(card);
					break;
				default:
					int numericNum = Integer.parseInt(""+num);
					card.setNumeric(numericNum);
					this.getComboList().get(numericNum-1).add(card);
			}			
			// Fill in the corresponding suit list for each card
			switch(suit){
				case 'd':
					this.getDiamondList().add(card);
					break;
				case 'c':
					this.getClubList().add(card);
					break;
				case 'h':
					this.getHeartList().add(card);
					break;
				case 's':
					this.getSpadeList().add(card);
					break;
			}
		}
	}
	
	/**
	 * Calculate the top rank cards
	 */
	public void calculateTopRankCards(){
		this.setHighRankCardList(this.findStraightFlush());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_STRAIGHT_FLUSH);
			return;
		}
		this.setHighRankCardList(this.find4OfAKind());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_4_OF_A_KIND);
			return;
		}		
		this.setHighRankCardList(this.findFullHouse());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_FULL_HOUSE);
			return;
		}		
		this.setHighRankCardList(this.findFlush());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_FLUSH);
			return;
		}		
		this.setHighRankCardList(this.findStraight());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_STRAIGHT);
			return;
		}		
		this.setHighRankCardList(this.find3OfAKind());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_3_OF_A_KIND);
			return;
		}		
		this.setHighRankCardList(this.findTwoPair());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_TWO_PAIR);
			return;
		}		
		this.setHighRankCardList(this.findOnePair());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_ONE_PAIR);
			return;
		}		
		this.setHighRankCardList(this.findHighCard());
		if(this.getHighRankCardList() != null){
			this.setHighRankHandType(HAND_TYPE_HIGH_CARD);
			return;
		}
	}
	
	/**
	 * Find the "Straight Flush"
	 * @return
	 */
	private List<Card> findStraightFlush(){
		List<Integer> idxChain = new ArrayList<Integer>();
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()==0){
				idxChain.clear();
			}else{	// There are cards in chain
				if(idxChain.size() == NUM_TOTAL_CARDS){	// If full, remove the first (this biggest one)
					idxChain.remove(0);
				}
				idxChain.add(i);
				if(idxChain.size() == NUM_TOTAL_CARDS){	// Check there is match on the suit
					List<Card> dList = new ArrayList<Card>();
					List<Card> cList = new ArrayList<Card>();
					List<Card> hList = new ArrayList<Card>();
					List<Card> sList = new ArrayList<Card>();
					for(Integer idx : idxChain){
						for(Card card : this.getComboList().get(idx)){
							if(card.getSuit().equals("d"))
								dList.add(card);
							else if(card.getSuit().equals("c"))
								cList.add(card);
							else if(card.getSuit().equals("h"))
								hList.add(card);
							else if(card.getSuit().equals("s"))
								sList.add(card);
						}
					}
					if(this.isTwoThreeHandBoard(dList))
						return dList;
					if(this.isTwoThreeHandBoard(cList))
						return cList;
					if(this.isTwoThreeHandBoard(hList))
						return hList;
					if(this.isTwoThreeHandBoard(sList))
						return sList;					
				}
			}
		}
		return null;
	}
	
	/**
	 * Find the "4-of-a-Kind"
	 * @return
	 */
	private List<Card> find4OfAKind(){
		List<Card> rtvCardList = new ArrayList<Card>();
		int idxMatchFound = -1;
		boolean needHand = false;
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()==4){
				NumHandBoardResult numResult = this.calculateHandBoardResult(this.getComboList().get(i));
				if(numResult.getNumHand()==1){	// Need one more from hand
					idxMatchFound = i;
					needHand = true;
					rtvCardList.addAll(this.getComboList().get(i));
				}else if(numResult.getNumHand()==2){	// Need one more from board
					idxMatchFound = i;
					needHand = false;
					rtvCardList.addAll(this.getComboList().get(i));
				}
				// Find the extra one card
				if(idxMatchFound != -1){
					for(int j=this.getComboList().size()-1; j>=0; j--){
						if(j != idxMatchFound){	// Skip the matching index
							for(Card card : this.getComboList().get(j)){
								if((needHand && card.getBelongsTo()==Card.BELONGS_TO_HAND) || 
									(!needHand && card.getBelongsTo()==Card.BELONGS_TO_BOARD)){
									rtvCardList.add(card);
									return rtvCardList;
								}
							}
						}
					}
				}
			}
			idxMatchFound = -1;
		}

		return null;
	}
	
	/**
	 * Find the "Full House"
	 * @return
	 */
	private List<Card> findFullHouse(){
		List<Card> rtvCardList = new ArrayList<Card>();
		int idxMatchFound = -1;
		int numHandLeft = 0;
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()>=3){	
				NumHandBoardResult numResult = this.calculateHandBoardResult(this.getComboList().get(i));
				if(numResult.getNumHand()==0 && numResult.getNumBoard()<=3){	// Need two more from hand
					idxMatchFound = i;
					numHandLeft = 2;
					rtvCardList.addAll(this.getComboList().get(i));
				}else if(numResult.getNumHand()==1){	// Need one more from hand
					idxMatchFound = i;
					numHandLeft = 1;
					rtvCardList.addAll(fitNumHandBoardCardsIn(this.getComboList().get(i),1,2));
				}else if(numResult.getNumHand()==2){	// Don't need hand
					idxMatchFound = i;
					numHandLeft = 0;
					rtvCardList.addAll(fitNumHandBoardCardsIn(this.getComboList().get(i),2,1));
				}
				// Find the extra one card
				if(idxMatchFound != -1){
					for(int j=this.comboList.size()-1; j>=0; j--){
						if(j != idxMatchFound){	// Skip the matching index
							NumHandBoardResult numResult2 = this.calculateHandBoardResult(this.getComboList().get(j));
							if(numHandLeft==0 && numResult2.getNumBoard()>=2){
								rtvCardList.addAll(fitNumHandBoardCardsIn(this.getComboList().get(j),0,2));
								return rtvCardList;
							}
							if(numHandLeft==1 && numResult2.getNumBoard()>=1 && numResult2.getNumHand()>=1){
								rtvCardList.addAll(fitNumHandBoardCardsIn(this.getComboList().get(j),1,1));
								return rtvCardList;
							}
							if(numHandLeft==2 && numResult2.getNumHand()>=2){
								rtvCardList.addAll(fitNumHandBoardCardsIn(this.getComboList().get(j),2,0));
								return rtvCardList;
							}
						}
					}
				}				
			}
		}
		return null;
	}
	
	/**
	 * Find the "Flush"
	 * @return
	 */
	private List<Card> findFlush(){
		if(this.getDiamondList().size()>=NUM_TOTAL_CARDS){
			return findHighRankFlushInTheSameSuit(this.getDiamondList());
		}
		if(this.getClubList().size()>=NUM_TOTAL_CARDS){
			return findHighRankFlushInTheSameSuit(this.getClubList());
		}
		if(this.getHeartList().size()>=NUM_TOTAL_CARDS){
			return findHighRankFlushInTheSameSuit(this.getHeartList());
		}
		if(this.getSpadeList().size()>=NUM_TOTAL_CARDS){
			return findHighRankFlushInTheSameSuit(this.getSpadeList());
		}		
		return null;
	}

	/**
	 * Find the "Straight"
	 * @return
	 */
	private List<Card> findStraight(){
		List<Integer> idxChain = new ArrayList<Integer>();
		for(int i=this.comboList.size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()==0){
				idxChain.clear();
			}else{	// There are cards in chain
				if(idxChain.size() == NUM_TOTAL_CARDS){	// If full, remove the first (this biggest one)
					idxChain.remove(0);
				}
				idxChain.add(i);
				if(idxChain.size() == NUM_TOTAL_CARDS){
					// Create index list card map with one list has maximum one hand card and one board card
					Map<Integer, List<Card>> idxCardListMap = new HashMap<Integer, List<Card>>();
					// Create pick hand map to track which idx should pick hand card, if true, means pick hand card, if false, means pick board card
					// otherwise, wait next step to clarify
					Map<Integer, Boolean> pickHandMap = new HashMap<Integer, Boolean>();
					int idxHasHand=0,idxHasBoard=0;
					for(Integer idx : idxChain){
						List<Card> filteredList = new ArrayList<Card>();
						boolean hasHand=false,hasBoard=false;
						for(Card card : this.getComboList().get(idx)){
							if(card.getBelongsTo()==Card.BELONGS_TO_HAND && !hasHand){
								filteredList.add(card);
								hasHand = true;
								idxHasHand++;
							}
							if(card.getBelongsTo()==Card.BELONGS_TO_BOARD && !hasBoard){
								filteredList.add(card);
								hasBoard = true;
								idxHasBoard++;
							}
						}
						idxCardListMap.put(idx, filteredList);	
						if(hasHand&&!hasBoard){	// Only has hand card
							pickHandMap.put(idx, true);
						}
						if(hasBoard&&!hasHand){	// Only has board card
							pickHandMap.put(idx, false);
						}
					}
					if(idxHasHand>=NUM_HAND_CARDS&&idxHasBoard>=NUM_BOARD_CARDS){	// Need to have minimum 2 hand idx and 3 board idx to continue
						int numHandUsed=0,numBoardUsed=0;
						// Calculate how many hand card slots and board card slots used
						for(Integer idx : idxChain){
							if(pickHandMap.containsKey(idx)){
								if(pickHandMap.get(idx))
									numHandUsed++;
								else
									numBoardUsed++;
							}
						}
						// Clarify the rest slots
						for(Integer idx : idxChain){
							if(!pickHandMap.containsKey(idx)){
								if(numHandUsed<NUM_HAND_CARDS){
									pickHandMap.put(idx, true);
									numHandUsed++;
								}
								if(numBoardUsed<NUM_BOARD_CARDS){
									pickHandMap.put(idx, false);
									numBoardUsed++;
								}
							}
						}
						// Finally pick the corresponding cards and return
						List<Card> rtvCardList = new ArrayList<Card>();
						for(Integer idx : idxChain){
							boolean pickHand = pickHandMap.get(idx);
							for(Card card : this.getComboList().get(idx)){
								if(card.getBelongsTo()==Card.BELONGS_TO_HAND && pickHand){
									rtvCardList.add(card);
									break;
								}
								if(card.getBelongsTo()==Card.BELONGS_TO_BOARD && !pickHand){
									rtvCardList.add(card);
									break;
								}								
							}
						}
						return rtvCardList;
					}
				}
			}
		}		
		return null;
	}
	
	/**
	 * Find the "3-of-a-Kind"
	 * @return
	 */
	private List<Card> find3OfAKind(){
		List<Card> rtvCardList = new ArrayList<Card>();
		int idxMatchFound = -1;
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()>=3){	
				NumHandBoardResult numResult = this.calculateHandBoardResult(this.getComboList().get(i));
				// Need the combination of 1 hand + 3 board OR 2 hand + 2 board
				if(numResult.getNumHand() <= 2){	// Hand card need to at maximum of 2
					idxMatchFound = i;
					// Add the 3 of them (Maybe 4)
					int numHandAdd=0;
					for(Card card : this.getComboList().get(i)){
						if(card.getBelongsTo()==Card.BELONGS_TO_HAND && numHandAdd < NUM_HAND_CARDS){
							rtvCardList.add(card);
							numHandAdd++;
						}
						if(card.getBelongsTo()==Card.BELONGS_TO_BOARD){
							rtvCardList.add(card);
						}
					}
					List<Card> skipCardList = new ArrayList<Card>();
					List<Integer> skipIdxList = new ArrayList<Integer>();
					skipIdxList.add(idxMatchFound);
					
					// Find the next missing cards
					this.fillInTheRestOfTheSingleCards(rtvCardList, skipIdxList, skipCardList);
					return rtvCardList;
				}			
			}
		}		
		return null;
	}
	
	/**
	 * Find the "Two Pair"
	 * @return
	 */
	private List<Card> findTwoPair(){
		boolean pairHasPureBoardCards = false;
		Map<Integer, List<Card>> pairMap = new HashMap<Integer, List<Card>>();
		List<Integer> idxFoundPair = new ArrayList<Integer>();
		for(int i=this.getComboList().size()-1; i>=0; i--){
			NumHandBoardResult numResult = this.calculateHandBoardResult(this.getComboList().get(i));
			if(this.getComboList().get(i).size()>=2){
				if(numResult.numBoard == this.getComboList().get(i).size()){
					if(!pairHasPureBoardCards){
						pairHasPureBoardCards = true;
						idxFoundPair.add(i);
						pairMap.put(i, this.getComboList().get(i));
					}
				}else{
					idxFoundPair.add(i);
					pairMap.put(i, this.getComboList().get(i));					
				}
			}
		}
		
		// Remove the duplicate 'A'
		if(idxFoundPair.contains(new Integer(0))&&idxFoundPair.contains(new Integer(13))){
			idxFoundPair.remove(new Integer(0));
			pairMap.remove(new Integer(0));
		}
		
		int numHandLeft=NUM_HAND_CARDS,numBoardLeft=NUM_BOARD_CARDS;
		if(pairMap.keySet().size()>=2){
			List<Card> rtvCardList = new ArrayList<Card>();
			int pairCounter=0;
			List<Integer> skipIdxList = new ArrayList<Integer>();
			for(Integer idx : idxFoundPair){
				int numPairSlotLeft=2;
				// a pair cards added for revert purpose
				List<Card> pairCardsAdded = new ArrayList<Card>();
				for(Card card : pairMap.get(idx)){
					if(card.getBelongsTo()==Card.BELONGS_TO_HAND && numHandLeft > 0){
						rtvCardList.add(card);
						numHandLeft--;
						numPairSlotLeft--;
						pairCardsAdded.add(card);
					}
					if(card.getBelongsTo()==Card.BELONGS_TO_BOARD && numBoardLeft > 0){
						rtvCardList.add(card);
						numBoardLeft--;
						numPairSlotLeft--;
						pairCardsAdded.add(card);
					}
					if(numPairSlotLeft==0){
						break;
					}
				}
				NumHandBoardResult numResult = this.calculateHandBoardResult(rtvCardList);
				boolean pairRevert = false;
				// If number of hand cards or boards cards exceed the limit, revert the last changes
				if(numResult.getNumHand()>NUM_HAND_CARDS || numResult.getNumBoard()>NUM_BOARD_CARDS || numPairSlotLeft!=0){
					for(Card toRemoveCard : pairCardsAdded){
						if(rtvCardList.contains(toRemoveCard)){
							if(toRemoveCard.getBelongsTo()==Card.BELONGS_TO_HAND)
								numHandLeft++;
							else
								numBoardLeft++;
							rtvCardList.remove(toRemoveCard);
							pairRevert = true;
						}
					}
				}
				if(!pairRevert){
					skipIdxList.add(idx);
					pairCounter++;
					if(pairCounter==2){
						break;
					}
				}
			}
			if(pairCounter==2){
				// Add extract single cards
				List<Card> skipCardList = new ArrayList<Card>();
				this.fillInTheRestOfTheSingleCards(rtvCardList, skipIdxList, skipCardList);			
				return rtvCardList;
			}
		}
		return null;
	}
	
	/**
	 * Find the "One Pair"
	 * @return
	 */
	private List<Card> findOnePair(){
		List<Card> rtvCardList = new ArrayList<Card>();
		List<Integer> skipIdxList = new ArrayList<Integer>();
		int numHandLeft=NUM_HAND_CARDS,numBoardLeft=NUM_BOARD_CARDS;
		boolean pairFound = false;
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(this.getComboList().get(i).size()>=2){
				pairFound = true;
				skipIdxList.add(i);
				for(Card card : this.getComboList().get(i)){
					if(card.getBelongsTo()==Card.BELONGS_TO_HAND && numHandLeft > 0){
						rtvCardList.add(card);
						numHandLeft--;
					}
					if(card.getBelongsTo()==Card.BELONGS_TO_BOARD && numBoardLeft > 0){
						rtvCardList.add(card);
						numBoardLeft--;
					}
				}
			}
			if(pairFound)
				break;
		}
		if(pairFound){
			// Add extract single cards
			List<Card> skipCardList = new ArrayList<Card>();
			this.fillInTheRestOfTheSingleCards(rtvCardList, skipIdxList, skipCardList);
			return rtvCardList;
		}
		return null;
	}
	
	/**
	 * Find the "High Card"
	 * @return
	 */
	private List<Card> findHighCard(){
		List<Card> rtvCardList = new ArrayList<Card>();
		List<Integer> skipIdxList = new ArrayList<Integer>();
		List<Card> skipCardList = new ArrayList<Card>();
		this.fillInTheRestOfTheSingleCards(rtvCardList, skipIdxList, skipCardList);
		return rtvCardList;
	}
	
	/**
	 * Find the high rank flush in the same suit cards
	 * @param cardsInSameSuit
	 * @return
	 */
	private List<Card> findHighRankFlushInTheSameSuit(List<Card> cardsInSameSuit){
		List<Card> rtvCardList = new ArrayList<Card>();
		List<Card> handList = new ArrayList<Card>();
		List<Card> boardList = new ArrayList<Card>();
		for(Card card : cardsInSameSuit){
			if(card.getBelongsTo()==Card.BELONGS_TO_HAND)
				handList.add(card);
			else
				boardList.add(card);
		}
		if(handList.size() >=2 && boardList.size() >=3){
			// Sort the suit card list by numeric number
			Collections.sort(handList, new CardComparator());
			Collections.sort(boardList, new CardComparator());
			for(int i=handList.size()-1; i>=handList.size()-2; i--){
				rtvCardList.add(handList.get(i));
			}
			for(int i=boardList.size()-1; i>=boardList.size()-3; i--){
				rtvCardList.add(boardList.get(i));
			}
			return rtvCardList;
		}
		return null;
	}
	
	/**
	 * Find the single high rank card according to the parameters. 
	 * @param skipIdxList
	 * @param cardBelongsTo: -1 no not care; 0 hand card; 1 board card
	 * @param skipCardList
	 * @return
	 */
	private Card findSingleHighRankCard(List<Integer> skipIdxList, int cardBelongsTo, List<Card> skipCardList){
		if(skipIdxList.contains(13)){	// If contains 'A' need to skip two index
			skipIdxList.add(0);
		}
		for(int i=this.getComboList().size()-1; i>=0; i--){
			if(!skipIdxList.contains(i)){
				for(Card card : this.getComboList().get(i)){
					if(!skipCardList.contains(card)){
						if(cardBelongsTo == -1){
							return card;
						}else if(card.getBelongsTo()==cardBelongsTo){
							return card;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Fill in the missing cards
	 * @param oriCardList
	 * @param skipIdxList
	 * @param skipCardList
	 */
	private void fillInTheRestOfTheSingleCards(List<Card> oriCardList, List<Integer> skipIdxList, List<Card> skipCardList){
		NumHandBoardResult numResult = this.calculateHandBoardResult(oriCardList);
		int numHandCard=numResult.getNumHand();
		int numBoardCard=numResult.getNumBoard();
		while(oriCardList.size()<NUM_TOTAL_CARDS){
			Card card = null;
			if(numHandCard < NUM_HAND_CARDS && numBoardCard < NUM_BOARD_CARDS){	// Missing both cards
				card = this.findSingleHighRankCard(skipIdxList, -1, skipCardList);
				if(card.getBelongsTo()==Card.BELONGS_TO_HAND)
					numHandCard++;
				else
					numBoardCard++;
			}else if(numHandCard == NUM_HAND_CARDS && numBoardCard < NUM_BOARD_CARDS){	// Missing board cards
				card = this.findSingleHighRankCard(skipIdxList, Card.BELONGS_TO_BOARD, skipCardList);
				numBoardCard++;
			}else if(numHandCard < NUM_HAND_CARDS && numBoardCard == NUM_BOARD_CARDS){	// Missing hand cards
				card = this.findSingleHighRankCard(skipIdxList, Card.BELONGS_TO_HAND, skipCardList);
				numHandCard++;
			}
			if(card != null){
				oriCardList.add(card);
				skipCardList.add(card);
			}
		}
	}

	private List<Card> fitNumHandBoardCardsIn(List<Card> oriCardList, int numHand, int numBoard){
		int currentHand=0, currentBoard=0;
		List<Card> rtvCardList = new ArrayList<Card>();
		for(Card card : oriCardList){
			if(card.getBelongsTo()==Card.BELONGS_TO_HAND && currentHand < numHand){
				rtvCardList.add(card);
				currentHand++;
			}else if(card.getBelongsTo()==Card.BELONGS_TO_BOARD && currentBoard < numBoard){
				rtvCardList.add(card);
				currentBoard++;
			}
		}
		return rtvCardList;
	}
	
	/**
	 * Check whether the cardList is a combination of 2 hand cards and 3 board cards
	 * @param cardList
	 * @return
	 */
	private boolean isTwoThreeHandBoard(List<Card> cardList){
		if(cardList.size() != NUM_TOTAL_CARDS){
			return false;
		}
		NumHandBoardResult result = this.calculateHandBoardResult(cardList);
		return (result.getNumHand()==2&&result.getNumBoard()==3);
	}
	
	/**
	 * Calculate the number of hand cards and board cards
	 * @param cardList
	 * @return
	 */
	private NumHandBoardResult calculateHandBoardResult(List<Card> cardList){
		int numHand=0,numBoard=0;
		for(Card card : cardList){
			if(card.getBelongsTo() == Card.BELONGS_TO_HAND){
				numHand++;
			}else{
				numBoard++;
			}
		}
		NumHandBoardResult result = new NumHandBoardResult();
		result.setNumHand(numHand);
		result.setNumBoard(numBoard);
		return result;
	}
	
	/**
	 * Compare to another combination
	 * @param combintion
	 * @return
	 */
	public int compareTo(OneCombination combintion){
		if(this.getHighRankHandType() < combintion.getHighRankHandType()){
			return -1;
		}else if(this.getHighRankHandType() > combintion.getHighRankHandType()){
			return 1;
		}else{
			Collections.sort(this.getHighRankCardList(), new CardComparator());
			Collections.sort(combintion.getHighRankCardList(), new CardComparator());
			
			switch(this.getHighRankHandType()){
				case HAND_TYPE_HIGH_CARD:
					int numA=0,numB=0;
					for(int i=4; i>=0; i--){
						numA = this.getHighRankCardList().get(i).getNumeric();
						numB = combintion.getHighRankCardList().get(i).getNumeric();
						if(numA == numB){
							continue;
						}else{
							break;
						}
					}
					return Integer.valueOf(numA).compareTo(Integer.valueOf(numB));
				case HAND_TYPE_ONE_PAIR:
					// Find the pair
					int pairNumA=0,pairNumB=0;
					for(int i=0; i<5; i++){
						int curNumeric = this.getHighRankCardList().get(i).getNumeric();
						if(pairNumA==curNumeric)
							break;
						else
							pairNumA = curNumeric;
					}
					for(int i=0; i<5; i++){
						int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
						if(pairNumB==curNumeric)
							break;
						else
							pairNumB = curNumeric;
					}
					if(pairNumA != pairNumB){
						return Integer.valueOf(pairNumA).compareTo(Integer.valueOf(pairNumB));
					}else{	// Same pair number
						List<Integer> skipNumericList = new ArrayList<Integer>();
						skipNumericList.add(pairNumA);
						int numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
						int numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
						while(numericA != -1 || numericB != -1){
							if(numericA != numericB){
								return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
							}else{
								skipNumericList.add(numericA);
								numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
								numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
							}
						}
					}
					return 0;
				case HAND_TYPE_TWO_PAIR:
					// Find the first pair
					int pairNumAa=0,pairNumBa=0;
					for(int i=0; i<5; i++){
						int curNumeric = this.getHighRankCardList().get(i).getNumeric();
						if(pairNumAa==curNumeric)
							break;
						else
							pairNumAa = curNumeric;
					}
					for(int i=0; i<5; i++){
						int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
						if(pairNumBa==curNumeric)
							break;
						else
							pairNumBa = curNumeric;
					}
					if(pairNumAa != pairNumBa){
						return Integer.valueOf(pairNumAa).compareTo(Integer.valueOf(pairNumBa));
					}else{	// Find the second pair
						int pairNumAb=0,pairNumBb=0;
						for(int i=0; i<5; i++){
							int curNumeric = this.getHighRankCardList().get(i).getNumeric();
							if(pairNumAb==curNumeric){
								break;
							}else if(curNumeric != pairNumAa){
								pairNumAb = curNumeric;
							}
						}
						for(int i=0; i<5; i++){
							int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
							if(pairNumBb==curNumeric){
								break;
							}else if(curNumeric != pairNumBa){
								pairNumBb = curNumeric;
							}
						}
						if(pairNumAb != pairNumBb){
							return Integer.valueOf(pairNumAb).compareTo(Integer.valueOf(pairNumBb));
						}else{	// Find the last card
							List<Integer> skipNumericList = new ArrayList<Integer>();
							skipNumericList.add(pairNumAa);
							skipNumericList.add(pairNumAb);
							int numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
							int numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
							if(numericA != numericB){
								return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
							}							
						}
					}
					return 0;
				case HAND_TYPE_3_OF_A_KIND:
					int threeValueA=0,threeValueB=0;
					int repeatA=0,repeatB=0;
					for(int i=0; i<5; i++){
						int curNumeric = this.getHighRankCardList().get(i).getNumeric();
						if(threeValueA==curNumeric){
							if(repeatA==2){
								break;
							}else{
								repeatA++;
							}
						}else{
							threeValueA = curNumeric;
							repeatA = 1;
						}
					}
					for(int i=0; i<5; i++){
						int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
						if(threeValueB==curNumeric){
							if(repeatB==2){
								break;
							}else{
								repeatB++;
							}
						}else{
							threeValueB = curNumeric;
							repeatB = 1;
						}
					}
					
					if(threeValueA != threeValueB){
						return Integer.valueOf(threeValueA).compareTo(Integer.valueOf(threeValueB));
					}else{	// Find the second last card
						List<Integer> skipNumericList = new ArrayList<Integer>();
						skipNumericList.add(threeValueA);
						int numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
						int numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
						if(numericA != numericB){
							return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
						}else{	// Find the last card
							skipNumericList.add(numericA);
							numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
							numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
							if(numericA != numericB){
								return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
							}							
						}
					}
					return 0;
				case HAND_TYPE_STRAIGHT:
					// Need to fix the 'A' problem as when sorting, 'A' will always append to the end even if in low card: A,2,3,4,5
					if(this.getHighRankCardList().get(0).getNumeric()==2 && 
						this.getHighRankCardList().get(4).getNumeric()==14){
						Card aceCard = this.getHighRankCardList().remove(4);
						this.getHighRankCardList().add(0, aceCard);
					}
					if(combintion.getHighRankCardList().get(0).getNumeric()==2 && 
							combintion.getHighRankCardList().get(4).getNumeric()==14){
							Card aceCard = combintion.getHighRankCardList().remove(4);
							combintion.getHighRankCardList().add(0, aceCard);
						}					
					int straightA = this.getHighRankCardList().get(4).getNumeric();
					int straightB = combintion.getHighRankCardList().get(4).getNumeric();
					return Integer.valueOf(straightA).compareTo(Integer.valueOf(straightB));
				case HAND_TYPE_FLUSH:
					List<Integer> skipNumericList = new ArrayList<Integer>();
					int numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
					int numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
					while(numericA != -1 || numericB != -1){
						if(numericA != numericB){
							return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
						}else{
							skipNumericList.add(numericA);
							numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
							numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
						}
					}					
					return 0;
				case HAND_TYPE_FULL_HOUSE:
					threeValueA=0;
					threeValueB=0;
					repeatA=0;
					repeatB=0;
					for(int i=0; i<5; i++){
						int curNumeric = this.getHighRankCardList().get(i).getNumeric();
						if(threeValueA==curNumeric){
							if(repeatA==2){
								break;
							}else{
								repeatA++;
							}
						}else{
							threeValueA = curNumeric;
							repeatA = 1;
						}
					}
					for(int i=0; i<5; i++){
						int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
						if(threeValueB==curNumeric){
							if(repeatB==2){
								break;
							}else{
								repeatB++;
							}
						}else{
							threeValueB = curNumeric;
							repeatB = 1;
						}
					}
					if(threeValueA != threeValueB){
						return Integer.valueOf(threeValueA).compareTo(Integer.valueOf(threeValueB));
					}else{	
						skipNumericList = new ArrayList<Integer>();
						skipNumericList.add(threeValueA);						
						numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
						numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
						if(numericA != numericB){
							return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
						}						
					}
					return 0;
				case HAND_TYPE_4_OF_A_KIND:
					int fourValueA=0,fourValueB=0;
					int repeat4A=0,repeat4B=0;
					for(int i=0; i<5; i++){
						int curNumeric = this.getHighRankCardList().get(i).getNumeric();
						if(fourValueA==curNumeric){
							if(repeat4A==3){
								break;
							}else{
								repeat4A++;
							}
						}else{
							fourValueA = curNumeric;
							repeat4A = 1;
						}
					}
					for(int i=0; i<5; i++){
						int curNumeric = combintion.getHighRankCardList().get(i).getNumeric();
						if(fourValueB==curNumeric){
							if(repeat4B==3){
								break;
							}else{
								repeat4B++;
							}
						}else{
							fourValueB = curNumeric;
							repeat4B = 1;
						}
					}
					
					if(fourValueA != fourValueB){
						return Integer.valueOf(fourValueA).compareTo(Integer.valueOf(fourValueB));
					}else{	// Find the last card
						skipNumericList = new ArrayList<Integer>();
						skipNumericList.add(fourValueA);
						numericA = this.findNextHighRankCard(this.getHighRankCardList(), skipNumericList);
						numericB = this.findNextHighRankCard(combintion.getHighRankCardList(), skipNumericList);
						if(numericA != numericB){
							return Integer.valueOf(numericA).compareTo(Integer.valueOf(numericB));
						}
					}					
					return 0;
				case HAND_TYPE_STRAIGHT_FLUSH:
					// Need to fix the 'A' problem as when sorting, 'A' will always append to the end even if in low card: A,2,3,4,5
					if(this.getHighRankCardList().get(0).getNumeric()==2 && 
						this.getHighRankCardList().get(4).getNumeric()==14){
						Card aceCard = this.getHighRankCardList().remove(4);
						this.getHighRankCardList().add(0, aceCard);
					}
					if(combintion.getHighRankCardList().get(0).getNumeric()==2 && 
							combintion.getHighRankCardList().get(4).getNumeric()==14){
							Card aceCard = combintion.getHighRankCardList().remove(4);
							combintion.getHighRankCardList().add(0, aceCard);
						}					
					straightA = this.getHighRankCardList().get(4).getNumeric();
					straightB = combintion.getHighRankCardList().get(4).getNumeric();
					return Integer.valueOf(straightA).compareTo(Integer.valueOf(straightB));
			}
			return 0;
		}
	}
	
	/**
	 * Find next high rank card number, if not found, -1 is return
	 * @param sortedCardList: 5 sorted card list
	 * @param skipNumericList
	 * @return
	 */
	private int findNextHighRankCard(List<Card> sortedCardList, List<Integer> skipNumericList){
		for(int i=4; i>=0; i--){
			int curNumeric = sortedCardList.get(i).getNumeric();
			if(skipNumericList == null || !skipNumericList.contains(curNumeric))
				return 	curNumeric;
		}
		return -1;
	}
	
	public String outputCardChain(){
		String rtv = "";
		for(Card card : this.getHighRankCardList()){
			if(!rtv.equals(""))
				rtv += "-";
			rtv += card.getNum() + card.getSuit();
		}
		return rtv;
	}
	
	public String getHighRankHandTypeS(){
		switch(this.getHighRankHandType()){
			case HAND_TYPE_HIGH_CARD:
				return "High Card";
			case HAND_TYPE_ONE_PAIR:
				return "One Pair";
			case HAND_TYPE_TWO_PAIR:
				return "Two Pair";
			case HAND_TYPE_3_OF_A_KIND:
				return "3-of-a-Kind";
			case HAND_TYPE_STRAIGHT:
				return "Straight";
			case HAND_TYPE_FLUSH:
				return "Flush";
			case HAND_TYPE_FULL_HOUSE:
				return "Full House";
			case HAND_TYPE_4_OF_A_KIND:
				return "4-of-a-Kind";
			case HAND_TYPE_STRAIGHT_FLUSH:
				return "Straight Flush";
			default:
				return "";
		}	
	}

	public List<List<Card>> getComboList() {
		return comboList;
	}

	public void setComboList(List<List<Card>> comboList) {
		this.comboList = comboList;
	}

	public List<Card> getDiamondList() {
		return diamondList;
	}

	public void setDiamondList(List<Card> diamondList) {
		this.diamondList = diamondList;
	}

	public List<Card> getClubList() {
		return clubList;
	}

	public void setClubList(List<Card> clubList) {
		this.clubList = clubList;
	}

	public List<Card> getHeartList() {
		return heartList;
	}

	public void setHeartList(List<Card> heartList) {
		this.heartList = heartList;
	}

	public List<Card> getSpadeList() {
		return spadeList;
	}

	public void setSpadeList(List<Card> spadeList) {
		this.spadeList = spadeList;
	}
	
	public List<Card> getHighRankCardList() {
		return highRankCardList;
	}

	public void setHighRankCardList(List<Card> highRankCardList) {
		this.highRankCardList = highRankCardList;
	}

	public int getHighRankHandType() {
		return highRankHandType;
	}

	public void setHighRankHandType(int highRankHandType) {
		this.highRankHandType = highRankHandType;
	}

	
	public class NumHandBoardResult{
		private int idx;
		private int numHand;
		private int numBoard;
		
		public int getIdx() {
			return idx;
		}
		public void setIdx(int idx) {
			this.idx = idx;
		}
		public int getNumHand() {
			return numHand;
		}
		public void setNumHand(int numHand) {
			this.numHand = numHand;
		}
		public int getNumBoard() {
			return numBoard;
		}
		public void setNumBoard(int numBoard) {
			this.numBoard = numBoard;
		}
	}
}
