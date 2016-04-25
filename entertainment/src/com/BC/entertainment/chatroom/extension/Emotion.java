package com.BC.entertainment.chatroom.extension;

/**
 * custom attachment emotion
 * 
 * @author wen zhong
 *
 */
public enum Emotion implements BaseEmotion{
	
	emotion_car(1,"保姆车", 100, 1000)
	;
	
	private int category;//emotion kind
	
	private String name;
	
	private int value;//value
	
	private int exPoints;//extra points
	
	Emotion(int category, String name, int value, int exPoints)
	{
		this.category = category;
		this.name = name;
		this.value = value;
		this.exPoints = exPoints;
	}
	
	/**
	 * get emotion by category
	 * @param category
	 * @return
	 */
	public static Emotion enumOfCategory(int category)
	{
		for (Emotion emotion : values())
		{
			if(emotion.getCategory() == category)
			{
				return emotion;
			}
		}
		return null;
	}
	
	public int getCategory()
	{
		return category;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public int getExPoints()
	{
		return exPoints;
	}
}