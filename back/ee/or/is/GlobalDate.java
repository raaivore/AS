/*
 * Created on 21.10.2006 by or
 *
 */
package ee.or.is;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The Class GlobalDate.
 */
public abstract class GlobalDate 
{
    
    /**
     * Checks if is new day.
     * 
     * @param lSTime the l s time
     * @param lETime the l e time
     * 
     * @return true, if is new day
     */
    public static boolean isNewDay( long lSTime, long lETime)
    {
	    if( lSTime> 0 && lETime > 0 ){
			Calendar DateS = GregorianCalendar.getInstance();
			DateS.setTime( new java.util.Date( lSTime));
			Calendar DateE = GregorianCalendar.getInstance();
			DateE.setTime( new java.util.Date( lETime));
			return DateS.get( Calendar.DAY_OF_YEAR) != DateE.get( Calendar.DAY_OF_YEAR);
	    }
	    return false;
    } 
    public static boolean isNewMonth( long lSTime, long lETime)
    {
	    if( lSTime> 0 && lETime > 0 ){
			Calendar DateS = GregorianCalendar.getInstance();
			DateS.setTime( new java.util.Date( lSTime));
			Calendar DateE = GregorianCalendar.getInstance();
			DateE.setTime( new java.util.Date( lETime));
			return DateS.get( Calendar.MONTH) != DateE.get( Calendar.MONTH);
	    }
	    return false;
    } 
    
    /**
     * Checks if is new day.
     * 
     * @param lETime the l e time
     * 
     * @return true, if is new day
     */
    public static boolean isNewDay( long lETime)
    {
        return isNewDay( ( new Date()).getTime(), lETime);   
    }
    public static boolean isNewMonth( long lETime)
    {
        return isNewMonth( ( new Date()).getTime(), lETime);   
    }
    
    /**
     * Gets the start day.
     * 
     * @param lSTime the l s time
     * 
     * @return the start day
     */
    public static long getStartDay( long lSTime)
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		int iY = DateS.get( Calendar.YEAR);
		int iM = DateS.get( Calendar.MONTH);
		int iD = DateS.get( Calendar.DAY_OF_MONTH);
		return ( new GregorianCalendar( iY, iM, iD, 00, 00)).getTime().getTime();
        
    }
    
    /**
     * Gets the start day.
     * 
     * @return the start day
     */
    public static long getStartDay()
    {
        return getStartDay( ( new Date()).getTime());
    }
    
    /**
     * Gets the start month.
     * 
     * @return the start month
     */
    public static long getStartMonth()
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( ( new Date()).getTime()));
		int iY = DateS.get( Calendar.YEAR);
		int iM = DateS.get( Calendar.MONTH);
		return ( new GregorianCalendar( iY, iM, 1, 00, 00)).getTime().getTime();
        
    }
    public static Date getEndMonth()
    {
		Calendar aDate = GregorianCalendar.getInstance();
		aDate.add( Calendar.MONTH, 1);
		int iY = aDate.get( Calendar.YEAR);
		int iM = aDate.get( Calendar.MONTH);
		aDate = new GregorianCalendar( iY, iM, 1, 00, 00);
		aDate.add( Calendar.DAY_OF_MONTH, -1);
		return aDate.getTime();
    }
    
    /**
     * Gets the start month string.
     * 
     * @return the start month string
     */
    public static String getStartMonthString()
    {
    	return getDateTime( getDateTime( getStartMonth()), "dd.MM.yyyy");
    }
    
    /**
     * Gets the date.
     * 
     * @param lSTime the l s time
     * 
     * @return the date
     */
    public static synchronized String getDate(  long lSTime)
    {
        if( lSTime == 0 ) lSTime = ( new Date()).getTime(); 
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyyMMdd"); 
		return DFOut.format( DateS.getTime());
    }
    public static String getDate()
    {
        return getDate( ( new Date()).getTime());
    }
    public static synchronized String getDateYM(  long lSTime)
    {
        if( lSTime == 0 ) lSTime = ( new Date()).getTime(); 
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyyMM"); 
		return DFOut.format( DateS.getTime());
    }
    public static String getDateYM()
    {
        return getDate( ( new Date()).getTime());
    }
    
    /**
     * Gets the time.
     * 
     * @param lSTime the l s time
     * 
     * @return the time
     */
    public static synchronized String getTime( java.sql.Date aTime)
    {
    	return getTime( aTime.getTime());
    }
    public static synchronized String getTime(  long lSTime)
    {
        if( lSTime == 0 ) lSTime = ( new Date()).getTime(); 
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		SimpleDateFormat DFOut = new SimpleDateFormat( "HH:mm:ss"); 
		return DFOut.format( DateS.getTime());
    }
    public static synchronized String getTimeMS(  long lSTime)
    {
        if( lSTime == 0 ) lSTime = ( new Date()).getTime(); 
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		SimpleDateFormat DFOut = new SimpleDateFormat( "HH:mm:ss.SSS"); 
		return DFOut.format( DateS.getTime());
    }
    
    /**
     * Gets the time hm.
     * 
     * @param lSTime the l s time
     * 
     * @return the time hm
     */
    public static synchronized String getTimeHM(  long lSTime)
    {
        if( lSTime == 0 ) lSTime = ( new Date()).getTime(); 
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lSTime));
		SimpleDateFormat DFOut = new SimpleDateFormat( "HH:mm"); 
		return DFOut.format( DateS.getTime());
    }
    
    /**
     * Gets the time.
     * 
     * @return the time
     */
    public static String getTime()
    {
        return getTime( ( new Date()).getTime());
    }
    public static String getTimeMS()
    {
        return getTimeMS( ( new Date()).getTime());
    }
	
	/**
	 * Gets the current date.
	 * 
	 * @return the current date
	 */
	public static java.sql.Date getCurrentDate() //throws MException 
	{
		GregorianCalendar Cal = new GregorianCalendar();
		return new java.sql.Date( Cal.getTimeInMillis());  
	}
	public static long getCurrentTime() //throws MException 
	{
		GregorianCalendar Cal = new GregorianCalendar();
		return Cal.getTimeInMillis();  
	}
     
    /**
     * Gets the current date time string.
     * 
     * @return the current date time string
     */
    public static String getCurrentDateTimeString()
    {
		Calendar Cal = GregorianCalendar.getInstance();
		java.sql.Date D = new java.sql.Date( Cal.getTimeInMillis());
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return new String( DFOut.format( D));
    }
    public static String getCurrentDateTimeStringS()
    {
		Calendar Cal = GregorianCalendar.getInstance();
		java.sql.Date D = new java.sql.Date( Cal.getTimeInMillis());
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss"); 
		return new String( DFOut.format( D));
    }
    public static String getCurrentDateString()
    {
		Calendar Cal = GregorianCalendar.getInstance();
		java.sql.Date D = new java.sql.Date( Cal.getTimeInMillis());
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( D));
    }
    
    /**
     * Gets the date string.
     * 
     * @param date the a date
     * 
     * @return the date string
     */
    public static String getDateString( Date date) 
    {
    	if( date == null ) return "";
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( date));
    }
    
    /**
     * Gets the date string.
     * 
     * @param lTime the l time
     * 
     * @return the date string
     */
    public static String getDateString( long lTime)
    {
		java.sql.Date D = new java.sql.Date( lTime);
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( D));
    }
    
    /**
     * Gets the date string.
     * 
     * @return the date string
     */
    public static String getDateString()
    {
		Calendar Cal = GregorianCalendar.getInstance();
		java.sql.Date D = new java.sql.Date( Cal.getTimeInMillis());
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( D));
    }
    
    /**
     * Gets the date time string.
     * 
     * @param Cal the cal
     * 
     * @return the date time string
     */
    public static String getDateTimeString( Calendar Cal)
    {
		java.sql.Date D = new java.sql.Date( Cal.getTimeInMillis());
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return new String( DFOut.format( D));
    }
    
    /**
     * Gets the date time string.
     * 
     * @param lTime the l time
     * 
     * @return the date time string
     */
    public static String getDateTimeString( long lTime)
    {
		java.sql.Date D = new java.sql.Date( lTime + 30000);
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return new String( DFOut.format( D));
    }
    public static String getDateTimeStringS( long lTime)
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( lTime + 500));
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss"); 
		return new String( DFOut.format( DateS.getTime()));
    }
    
    /**
     * Gets the date time string.
     * 
     * @param Time the time
     * 
     * @return the date time string
     */
    public static String getDateTimeString( java.sql.Date Time)
    {
		return ( Time != null)? getDateTimeString( Time.getTime()): null;
    }
    public static String getDateTimeStringS( java.sql.Date Time)
    {
		return ( Time != null)? getDateTimeStringS( Time.getTime()): null;
    }
    
    /**
     * Gets the date time.
     * 
     * @param lTime the l time
     * 
     * @return the date time
     */
    public static  java.sql.Date getDateTime( long lTime)
    {
	    if( lTime> 0 ){
			Calendar Cal = GregorianCalendar.getInstance();
			Cal.setTime( new java.util.Date( lTime));
			return  new java.sql.Date( Cal.getTimeInMillis());
	    }
	    return null;
    }
	
	/**
	 * Gets the date time.
	 * 
	 * @param DateValue the date value
	 * @param sFormat the s format
	 * 
	 * @return the date time
	 */
	public  static String getDateTime( java.sql.Date DateValue, String sFormat) 
	{
		if( DateValue == null || sFormat == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( sFormat); 
		return DFOut.format( DateValue);
	}
    
    /**
     * Gets the day.
     * 
     * @param iY the i y
     * @param iM the i m
     * @param iD the i d
     * 
     * @return the day
     */
    public static long getDay( int iY, int iM, int iD)
    {
//		Calendar DateS = GregorianCalendar.getInstance();
		return ( new GregorianCalendar( iY, iM, iD, 00, 00)).getTime().getTime();
    }
    public static long getTime( int iY, int iM, int iD, int iH, int iMin)
    {
//		Calendar DateS = GregorianCalendar.getInstance();
		return ( new GregorianCalendar( iY, iM, iD, iH, iMin)).getTime().getTime();
    }
    
    /**
     * Gets the time hm.
     * 
     * @param iTime the i time
     * 
     * @return the time hm
     */
    public static String getTimeHM( int iTime)
    {
		iTime += 30;
		int iH = iTime / 3600;
		int iM = (iTime % 3600) / 60;
		if( iH >= 24 ) iH -= 24;
		if( iH >= 10 ){
			if( iM >= 10 )
			    return iH + ":" + iM;
			else
			    return iH + ":0" + iM;
	 			
		}else{
			if( iM >= 10 )
			    return "0" + iH + ":" + iM;
			else
			    return "0" + iH + ":0" + iM;
		}
    }
    public static int parseTimeHM( String sTime)
    {
    	int iTime = 0;
    	int i = sTime.indexOf( ':');
    	if( i > 0 ){
    		int iH = Integer.parseInt( sTime.substring( 0, i));
    		int iM = Integer.parseInt( sTime.substring( ++i));
    		iTime = ( iH * 60 + iM) * 60;
    	}else{
    		iTime = Integer.parseInt( sTime) * 60 * 60;
    	}
    	return iTime;
    }
    public static String getTime( int iTime)
    {
		StringBuffer aBuf = new StringBuffer();
		if( iTime < 0 ){
			aBuf.append( "- ");
			iTime = -iTime;
		}
		int iH = iTime / 3600;
		int iM = (iTime % 3600) / 60;
		int iS = iTime - ((iH * 60) + iM) * 60;
		if( iH >= 24 ) iH -= 24;
		if( iH < 10 ) aBuf.append( "0");
		aBuf.append( iH);
		aBuf.append( ":");
		if( iM < 10 ) aBuf.append( "0");
		aBuf.append( iM);
		aBuf.append( ":");
		if( iS < 10 ) aBuf.append( "0");
		aBuf.append( iS);
	    return aBuf.toString();
    }
    
    /**
     * Gets the calendar.
     * 
     * @return the calendar
     */
    public static Calendar getCalendar()
    {
    	return new GregorianCalendar( TimeZone.getTimeZone("Europe/Tallinn"), new Locale( "et"));
    }
	
	/**
	 * Parses the date.
	 * 
	 * @param sDate the s date
	 * @param bStart the b start
	 * 
	 * @return the gregorian calendar
	 */
	public static Date parseDate( String sDate, boolean bStart) 
	{
		GregorianCalendar aCal = parseCalendar( sDate, bStart);
		return ( aCal != null)? aCal.getTime(): null;
	}
	public static GregorianCalendar parseCalendar( String sDate, boolean bStart) 
	{
		
		if( sDate == null ) return null;
		Calendar Date = getCalendar();

// Date fom dd.mm.yyyy hh:mm
//		java.sql.Date D = new java.sql.Date( ( new java.util.Date()).getTime());
		int iD=0, iM=0, iY=0, iH = 0, iMin = 0, iSec = 0;
	try{		
		int i = 0, n = sDate.length();
		char cSymb;

		while( !Character.isDigit( cSymb = sDate.charAt( i++)) && i < n);
		if( cSymb >= '0' && cSymb <= '9' ){
			iD = cSymb - '0';
			cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iD = iD*10 + cSymb - '0';
				cSymb = sDate.charAt( i++);
			}
		}
		if( cSymb == '.' || cSymb == '/'){
			cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iM = cSymb - '0';
				cSymb = sDate.charAt( i++);
				if( cSymb >= '0' && cSymb <= '9' ){
					iM = iM*10 + cSymb - '0';
					cSymb = sDate.charAt( i++);
				}
			}
			if( cSymb == '.' || cSymb == '/' ){
				cSymb = sDate.charAt( i++);
				if( cSymb >= '0' && cSymb <= '9' ){
					iY = cSymb - '0';
					cSymb = sDate.charAt( i++);
					if( cSymb >= '0' && cSymb <= '9' ){
						iY = iY*10 + cSymb - '0';
						cSymb = sDate.charAt( i++);
						if( cSymb >= '0' && cSymb <= '9' ){
							iY = iY*10 + cSymb - '0';
							cSymb = sDate.charAt( i++);
							if( cSymb >= '0' && cSymb <= '9' ){
								iY = iY*10 + cSymb - '0';
								cSymb = sDate.charAt( i++);
							}
						}
					}
				}
			}
		}else if( cSymb == ':' ){
			if( iD > 0 ){
				iH = iD;
				iD = 0;
			}			
		}
		if( cSymb != ':' ){
			while( cSymb  == ' ' ) cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iH = cSymb - '0';
				cSymb = sDate.charAt( i++);
				if( cSymb >= '0' && cSymb <= '9' ){
					iH = iH*10 + cSymb - '0';
					cSymb = sDate.charAt( i++);
				}
			}else if( !bStart ) iH = 24;

		}
		if( cSymb == ':' ||  cSymb == '.' ){
			cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iMin = cSymb - '0';
				cSymb = sDate.charAt( i++);
				if( cSymb >= '0' && cSymb <= '9' ){
					iMin = iMin*10 + cSymb - '0';
					cSymb = sDate.charAt( i++);
				}
				if( cSymb == ':' ||  cSymb == '.' ){
					cSymb = sDate.charAt( i++);
					if( cSymb >= '0' && cSymb <= '9' ){
						iSec = cSymb - '0';
						cSymb = sDate.charAt( i++);
						if( cSymb >= '0' && cSymb <= '9' ){
							iSec = iSec*10 + cSymb - '0';
						}
					}					
				}
			}
		}
		
	}catch( IndexOutOfBoundsException e ){
		if( iH == 0 && !bStart ) iH = 24;
	}
		if( iD == 0 ){
			if( iM == 0 ) iD = Date.get( GregorianCalendar.DAY_OF_MONTH);
			else{  // antud on kuu, p�ev andmata
				iD = 1;
				if( !bStart ) ++iM;
				iH = 0;
			}
		}
		if( iM == 0 ) iM =  Date.get( GregorianCalendar.MONTH);
		else --iM; // months starts from 0
		if( iY == 0 ) iY =  Date.get( GregorianCalendar.YEAR);
		else if( iY < 100 ) iY += 2000;
		if( iM >= 12 ){ iM -= 12; ++iY;}
//		if( iH < 0 ) iH = Date.get( GregorianCalendar.HOUR_OF_DAY);
//		if( iMin < 0 ) iMin =  Date.get( GregorianCalendar.MINUTE);

		return ( new GregorianCalendar( iY, iM, iD, iH, iMin, iSec));
	}
	
	/**
	 * Date to string24.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString24(  java.util.Date D) 
	{
		if( D == null ) return null;
		long lD24 = D.getTime() - 5 * 60 * 1000;
		java.util.Date D24 = getDateTime( lD24);
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy 24:00"); 
		return new String( DFOut.format( D24));
	}
	
	/**
	 * Gets the day seconds hm.
	 * 
	 * @param Date the date
	 * 
	 * @return the day seconds hm
	 */
	public static int getDaySecondsHM( java.sql.Date Date)
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( Date.getTime()));
		int iH = DateS.get( Calendar.HOUR_OF_DAY);
		int iM = DateS.get( Calendar.MINUTE);
		int iS = DateS.get( Calendar.SECOND);
		if( iS >= 30 ){
			if( ++iM >= 60 ){
				iM -= 60;
				++iH;
			}
		}
		return (iH*60 + iM) * 60;
    }
	
	/**
	 * Gets the day minutes.
	 * 
	 * @param Date the date
	 * 
	 * @return the day minutes
	 */
	public static int getDayMinutes( java.sql.Date Date)
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( new java.util.Date( Date.getTime()));
		int iH = DateS.get( Calendar.HOUR_OF_DAY);
		int iM = DateS.get( Calendar.MINUTE);
		return iH*60 + iM;
    }
	public static int getDay( java.sql.Date aDate)
    {
		Calendar aDateS = GregorianCalendar.getInstance();
		aDateS.setTime( new java.util.Date( aDate.getTime()));
		return aDateS.get( Calendar.DAY_OF_YEAR);
    }
	public static int getMonth( java.sql.Date aDate)
    {
		Calendar aDateS = GregorianCalendar.getInstance();
		aDateS.setTime( new java.util.Date( aDate.getTime()));
		return aDateS.get( Calendar.MONTH);
    }
	public static int getYear( java.sql.Date aDate)
    {
		Calendar aDateS = GregorianCalendar.getInstance();
		aDateS.setTime( new java.util.Date( aDate.getTime()));
		return aDateS.get( Calendar.YEAR);
    }
	public static int getDayFromStart( java.sql.Date aDate)
    {
		Calendar aDateS = GregorianCalendar.getInstance();
		aDateS.setTime( new java.util.Date( aDate.getTime()));
		long lDays = aDate.getTime() / 1000 / 60 / 60 / 24;
		return ( int)lDays;
    }
	public static int getDayFromStart( GregorianCalendar aDate)
    {
		long lDays = aDate.getTimeInMillis() / 1000 / 60 / 60 / 24;
		return ( int)lDays;
    }
	public static Date getDateFromStart( int iDays)
	{
		GregorianCalendar aDate = new GregorianCalendar( 1970, 0, 1, 0, 0);	
		long lDate = aDate.getTimeInMillis();	
		lDate += iDays * 24L * 60L * 60L * 1000L;
		return new java.sql.Date( lDate);
	}
	
	/**
	 * Gets the day seconds.
	 * 
	 * @param Date the date
	 * 
	 * @return the day seconds
	 */
	public static int getDaySeconds( java.sql.Date Date)
    {
		return getDaySeconds( new java.util.Date( Date.getTime()));
    }
	public static int getDaySeconds( java.util.Date Date)
    {
		Calendar DateS = GregorianCalendar.getInstance();
		DateS.setTime( Date);
		int iH = DateS.get( Calendar.HOUR_OF_DAY);
		int iM = DateS.get( Calendar.MINUTE);
		int iS = DateS.get( Calendar.SECOND);
		return (iH*60 + iM) * 60 + iS;
    }
	public static int getCurrentSeconds()
    {
		Calendar DateS = GregorianCalendar.getInstance();
		int iH = DateS.get( Calendar.HOUR_OF_DAY);
		int iM = DateS.get( Calendar.MINUTE);
		int iS = DateS.get( Calendar.SECOND);
		return (iH*60 + iM) * 60 + iS;
    }
	
	/**
	 * Gets the day minutes.
	 * 
	 * @param lTime the l time
	 * 
	 * @return the day minutes
	 */
	public static  int getDayMinutes( long lTime)
    {
	    if( lTime> 0 ){
			Calendar aDate = GregorianCalendar.getInstance();
			aDate.setTime( new java.util.Date( lTime));
			int iH = aDate.get( Calendar.HOUR_OF_DAY);
			int iM = aDate.get( Calendar.MINUTE);
			return iH*60 + iM;
	    }
	    return 0;
    }
	
	/**
	 * Gets the date time sql string.
	 * 
	 * @return the date time sql string
	 */
	public static String getDateTimeSQLString( long lTime)
	{
		return getDateTimeSQLString( new java.util.Date( lTime));
	}
	public static String getDateTimeSQLString()
	{
		return getDateTimeSQLString( getCurrentDate());
	}
	public static String getDateTimeSQLString( Date D)
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
		return DFOut.format( D);
	}
	public static String getTimeStamp( )
	{
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ"); 
		return DFOut.format( getCurrentDate());
	}
	public static String getDateSQLString()
	{
		return getDateSQLString( getCurrentDate());
	}
	public static String getDateSQLString( java.sql.Date D)
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd"); 
		return DFOut.format( D);
	}
	public static String getDateSQLString( Date D)
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd"); 
		return DFOut.format( D);
	}
    public static int getDays( long lSTime, long lETime)
    {
    	return ( lSTime > 0 && lETime > 0)? ( int)(lETime - lSTime) / 1000 / 3600 / 24: 0;
    } 
    public static int getDays( java.sql.Date aSDate, java.sql.Date aEDate)
    {
    	return (aSDate != null && aEDate != null)? getDays( aSDate.getTime(), aEDate.getTime()): 0;
    }
	public static String getDateString( String sDateSQL) throws MException 
	{
		String sDate = null;
		if( sDateSQL != null && sDateSQL.length() >0 ){
			SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd");	
			try{
				sDate = getDateString( DF.parse( sDateSQL));  
			} catch ( Exception e1) {
			}
		}
		return sDate; 
	}
	public static String getDateTimeString( String sDateSQL) throws MException 
	{
		String sDate = null;
		if( sDateSQL != null && sDateSQL.length() >0 ){
			SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");	
			try{
				java.util.Date date = DF.parse( sDateSQL);  
				sDate = getDateTimeString( new java.sql.Date( date.getTime())); 
			} catch (ParseException e) {
			}
		}
		return sDate; 
	}
	public static int DateToInt( String sDate)
	{
		int i = sDate.indexOf( "'");
		if( i >= 0 ){
			int j = sDate.indexOf( "'", ++i);
			if( j > 0) sDate = sDate.substring( i, j);
			else sDate = sDate.substring( i);
		}
		int iY = 0, iM = 0, iD = 0;
		i = sDate.indexOf( '.');
		if( i>0){
			iD = Integer.parseInt( sDate.substring( 0, i));
			int j = sDate.indexOf( '.', ++i);
			if( j > 0){
				iM = Integer.parseInt( sDate.substring( i, j));
				i = j+1;
			}else{
				iM = iD;
				iD = 0;
			}
		}else{
			i = sDate.indexOf( '-');
			if( i > 0 ){
				iY = Integer.parseInt( sDate.substring( 0, i));
				int j = sDate.indexOf( '-', ++i);
				if( j > 0){
					iM = Integer.parseInt( sDate.substring( i, j));
					iD = Integer.parseInt( sDate.substring( ++j));
				}else{
					iM = Integer.parseInt( sDate.substring( i));
				}
			}else i = 0;
		}
		if( iY == 0 ) iY = Integer.parseInt( sDate.substring( i));
		return iY*10000 + iM *100 + iD;
	}
	public static String getPeriodString( int iTime)
	{
		int iH = iTime / 3600;
		int iM = (iTime % 3600) / 60;
		int iS = iTime % 60;
		StringBuffer sTime = new StringBuffer( "");
		if( iH > 0 ){
			sTime.append( iH + ":");	
			if( iM >= 10 ) sTime.append( iM);
			else if( iM > 0 )  sTime.append( "0" + iM);		
			else sTime.append( "00");
			sTime.append( ".");
		}else if( iM > 0 ){
			sTime.append( iM);		
			sTime.append( ".");
		}
		if( iS >= 10 ) sTime.append( iS);
		else if( iH > 0 || iM > 0 )  sTime.append( "0" + iS);		
		else sTime.append( iS);
		return  sTime.toString();
	}
	public static String getPeriodString( long lSTime)
	{
		long lETime = ( new java.util.Date()).getTime() - lSTime;
		int iTimeS = ((int)lETime + 50) / 1000; 
		if( iTimeS > 0 ){
			int iTimeM = (int)(( lETime - iTimeS * 1000) / 100);
			return getPeriodString( iTimeS) + "," + iTimeM;
		}else{
			return lETime + " ms";
		}
//		int iTimeS = (int)(lETime + 500) / 1000; 
//		return getTimeString( iTimeS);
	}
	public static long getPeriod( Date aSTime)
	{
		long lETime = ( new java.util.Date()).getTime() - aSTime.getTime();
		return (lETime + 500) / 1000; 
	}
	public static String getPeriodMS( long lSTime)
	{
		long lETime = ( new java.util.Date()).getTime() - lSTime;
		return lETime + "ms";
	}
	public static int getCurrentHour()
    {
		GregorianCalendar aCal = new GregorianCalendar();
		return aCal.get( Calendar.HOUR_OF_DAY);
    }
	public static Date getCurrentStartOfDay() 
	{
		GregorianCalendar aCal = new GregorianCalendar();
        return clearTime( aCal.getTime());
    }
    public static Date clearTime( Date date) 
    {
        if( date == null ){ return null;}
        Calendar c = Calendar.getInstance();
        c.setTime( date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }    
	public static Date addDays( Date aDate, int iDays) 
	{
		GregorianCalendar aCal = new GregorianCalendar();
		aCal.setTime( aDate);
		aCal.add( GregorianCalendar.DAY_OF_MONTH, iDays);
        return new java.sql.Date( aCal.getTimeInMillis()); 
    }
	public static LocalDate getLocalDate( Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	public static boolean isEqual( Date aDate1, Date aDate2)
	{
		if( aDate1 != null && aDate2 != null ) {
			Calendar aCal1 = Calendar.getInstance();
			Calendar aCal2 = Calendar.getInstance();
			aCal1.setTime( aDate1);
			aCal2.setTime( aDate2);
			return aCal1.equals( aCal2);
		}
		return false;
    }
	public static String getMonthName(  Date aDate)
	{
		Calendar aDateS = GregorianCalendar.getInstance();
		aDateS.setTime( aDate);
		int iY = aDateS.get( Calendar.YEAR);
		int iM = aDateS.get( Calendar.MONTH);
		return ( new DateFormatSymbols()).getMonths()[ iM] + " " + iY;
	}
}
