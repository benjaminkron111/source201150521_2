package com.cic.rs;

public class Record {
	
	String delim = "c###c";
	
	String dno = "";
	String date = "";
	String orderid = "";
	String commodity = "";
	String count = "";
	String shipCount = "";
	String unit = "";
	String price = "";
	String amount = "";
	String branch = "";
	String status = "";
	String remark = "";
	
	public static final int COL_INDEX_DNO = 0;
	public static final int COL_INDEX_DATE = 1;
	public static final int COL_INDEX_ORDERID = 2;
	public static final int COL_INDEX_COMMODITY = 3;
	public static final int COL_INDEX_COUNT = 4;
	public static final int COL_INDEX_SHIPCOUNT = 5;
	public static final int COL_INDEX_UNIT = 6;
	public static final int COL_INDEX_PRICE = 7;
	public static final int COL_INDEX_AMOUNT = 8;
	public static final int COL_INDEX_BRANCH = 9;
	public static final int COL_INDEX_STATUS = 10;
	public static final int COL_INDEX_REMARK = 11;
	
	public String toString()
	{
		String str = this.dno + this.delim 
				+ this.date + this.delim 
				+ this.orderid + this.delim 
				+ this.commodity + this.delim 
				+ this.count + this.delim
				+ this.shipCount + this.delim
				+ this.unit + this.delim
				+ this.price + this.delim
				+ this.amount + this.delim
				+ this.branch + this.delim
				+ this.status + this.delim
				+ this.remark;
		
		return str;
	}
	
	public void update(int colIndex, String val)
	{
		switch(colIndex)
		{
		case COL_INDEX_DNO:
			this.dno = val;
			break;
		case COL_INDEX_DATE:
			this.date = val;
			break;
		case COL_INDEX_ORDERID:
			this.orderid = val;
			break;
		case COL_INDEX_COMMODITY:
			this.commodity = val;
			break;
		case COL_INDEX_COUNT:
			this.count = val;
			break;
		case COL_INDEX_UNIT:
			this.unit = val;			
			break;
		case COL_INDEX_PRICE:
			this.price = val;			
			break;
		case COL_INDEX_AMOUNT:
			this.amount = val;			
			break;
		case COL_INDEX_BRANCH:
			this.branch = val;			
			break;
		case COL_INDEX_STATUS:
			this.status = val;			
			break;		
		case COL_INDEX_SHIPCOUNT:
			this.shipCount = val;			
			break;				
		case COL_INDEX_REMARK:
			this.remark = val;			
			break;		
			
		
		}		
	}
	
	public void markAsRead()
	{
		this.status = "2";
	}
	
	public void fromString(String str)
	{
		String[] cols = str.split(this.delim);
		
		//if ( cols.length != 5) return;
		
		try
		{

			this.dno = cols[COL_INDEX_DNO];

			this.date = cols[COL_INDEX_DATE];
	
			this.orderid = cols[COL_INDEX_ORDERID];
		
			this.commodity = cols[COL_INDEX_COMMODITY];
			
			this.count = cols[COL_INDEX_COUNT];
			
			this.unit = cols[COL_INDEX_UNIT];			
			
			this.price = cols[COL_INDEX_PRICE];			
			
			this.amount = cols[COL_INDEX_AMOUNT];			
			
			this.branch = cols[COL_INDEX_BRANCH];			
			
			this.status = cols[COL_INDEX_STATUS];			
			
			this.remark = cols[COL_INDEX_REMARK];
			
			this.shipCount = cols[COL_INDEX_SHIPCOUNT];
			

					
		}
		catch(Exception e)
		{
			
		}

	}
	
	
}
