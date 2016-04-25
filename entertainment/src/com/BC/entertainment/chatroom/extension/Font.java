package com.BC.entertainment.chatroom.extension;

/**
 * custom attachment font
 * 
 * @author wen zhong
 *
 */
public enum Font {

	font_bxjj(1,"悲喜交加", 100, 1000)
	;
	
	private int category;//emotion kind
	
	private String name;
	
	private int value;//value
	
	private int exPoints;//extra points
	
	Font(int category, String name, int value, int exPoints)
	{
		this.category = category;
		this.name = name;
		this.value = value;
		this.exPoints = exPoints;
	}
	
	/**
	 * get font by category
	 * @param category
	 * @return
	 */
	public static Font enumOfCategory(int category)
	{
		for (Font font : values())
		{
			if(font.getCategory() == category)
			{
				return font;
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
