/*
 * 
 */
package ee.or.is;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
/**
 * The Class GlobalGIS.
 */
public class GlobalGIS 
{
	
	/**
	 * Gets the as ll.
	 * 
	 * @param fcoord the fcoord
	 * 
	 * @return the as ll
	 */
	public static final String getAsLL( double fcoord) 
	{
		return _getAsLL( fcoord, 2);
/*		int deg = ( int) fcoord;
		int min = ( int) ((fcoord - deg)*60);

		double dsec = ((fcoord - ((double)deg + (double)min/60))*3600.0);
		int iSec = ( int)Math.round( dsec*100);
/*		DecimalFormat format = new DecimalFormat("00.##");
		FieldPosition f = new FieldPosition(0);
		StringBuffer s = new StringBuffer();
		format.format(dsec, s, f);
		String sec = s.toString(); /
		
		StringBuffer scoord = new StringBuffer( 20);
		scoord.append( deg);
		scoord.append( ":");
		scoord.append( GlobalData.IntToStringZero( min, 2));
		scoord.append( ":");
		scoord.append( GlobalData.IntToStringZero( iSec/100, 2));
		scoord.append( ".");
		scoord.append( GlobalData.IntToStringZero( iSec%100, 2));
		return scoord.toString(); */
	}
	public static final String getAsLL( Point2D PLL) 
	{
		return (PLL!=null)? GlobalGIS.getAsLL( PLL.getY()) + " " +  GlobalGIS.getAsLL( PLL.getX()): "-";
	}
	public static final String getAsLL( Point2D PLL, int iPower) 
	{
		return (PLL!=null)? GlobalGIS._getAsLL( PLL.getY(), iPower) + " " +  GlobalGIS._getAsLL( PLL.getX(), iPower): "-";
	}
	public static final String getAsLL_M( double fcoord) 
	{
		return _getAsLL( fcoord, 6);
/*		int iDeg = ( int) fcoord;
		double dMin = ((fcoord - ((double)iDeg))*60);
		int iMin = ( int)( dMin + 0.0000005);
		int iSec = ( int)Math.round( (dMin - iMin)*1000000);
		if( iSec < 0 ) iSec = 0;
		
		StringBuffer scoord = new StringBuffer( 20);
		scoord.append( iDeg);
		scoord.append( ":");
		scoord.append( GlobalData.IntToStringZero( iMin, 2));
		scoord.append( ".");
		scoord.append( GlobalData.IntToStringZero( iSec, 6));
		return scoord.toString();*/
	}
	private static final String _getAsLL( double fcoord, int iPower) 
	{
		int iDeg = ( int) fcoord;
		double dMin = ((fcoord - ((double)iDeg))*60);
		int iMin = ( int)( dMin + Math.pow( 10, -iPower) * 0.5);
		int iSec = ( int)Math.round( (dMin - iMin)* Math.pow( 10, iPower));
		if( iSec < 0 ) iSec = 0;
		
		StringBuffer scoord = new StringBuffer( 20);
		scoord.append( iDeg);
		scoord.append( ":");
		scoord.append( GlobalData.IntToStringZero( iMin, 2));
		scoord.append( ".");
		scoord.append( GlobalData.IntToStringZero( iSec, iPower));
		return scoord.toString();
	}
	public static final String getAsLL_M( Point2D PLL) 
	{
		return (PLL!=null)? GlobalGIS.getAsLL_M( PLL.getY()) + " " +  GlobalGIS.getAsLL_M( PLL.getX()): "-";
	}
	public static final String getAsKML( Point2D PLL) 
	{
		return (PLL!=null)? GlobalGIS.getAsKML( PLL.getX()) + "," +  GlobalGIS.getAsKML( PLL.getY()): "";
	}
	public static String getAsKML( double dLL){ return GlobalData.DoubleToString( dLL, 6);}
	/**
	 * Gets the as ll.
	 * 
	 * @param PLL the pLL
	 * 
	 * @return the as ll
	 */
	
	/**
	 * Gets the from ll.
	 * 
	 * @param sCoord the s coord
	 * 
	 * @return the from ll
	 */
	public static final double getFromLL( String sCoord) 
	{
		try{
			if( sCoord != null && sCoord.trim().length() > 0 ){
				int i = sCoord.indexOf( ':');
				if( i > 0 ){
					double dDeg = Integer.parseInt( sCoord.substring( 0, i));
					int j = sCoord.indexOf( ':', ++i);
					if( j > 0 ){
						double dMin = Integer.parseInt( sCoord.substring( i, j));
						double dSec = Double.parseDouble( sCoord.substring( ++j));
						return dDeg + dMin / 60 + dSec / 3600;
					}else{
						double dMin = Double.parseDouble( sCoord.substring( i));
						return dDeg + dMin / 60;
					}
				}else{
					 return Double.parseDouble( sCoord);
				}
			}
		}catch( Exception aE ){
			Log.log( aE);
		}
		return -1;
	}
	
	/**
	 * Gets the coord from ll.
	 * 
	 * @param sCoord the s coord
	 * 
	 * @return the coord from ll
	 */
	public static final Point2D getCoordFromLL( String sCoord) 
	{
		int i = sCoord.indexOf( ' ');
		if( i > 0 ){
			double dX =getFromLL( sCoord.substring( 0, i)); 
			double dY =getFromLL( sCoord.substring( ++i)); 
			if( dX >= 0 && dY >= 0 ){
				if( dX < dY)
					return new Point2D.Double( dX, dY);
				else 
					return new Point2D.Double( dY, dX);
			}
		}
		return null;
	}
	public static final Point2D getCoordFromLL( String sCoordX, String sCoordY) 
	{
		double dX =getFromLL( sCoordX); 
		double dY =getFromLL( sCoordY); 
		if( dX >= 0 && dY >= 0 ) return new Point2D.Double( dX, dY);
		return null;
	}
	public static final Point2D.Double getCoordLL( String sCoord) 
	{
		int i = sCoord.indexOf( ' ');
		if( i > 0 ){
			double dY =getFromLL( sCoord.substring( 0, i)); 
			double dX =getFromLL( sCoord.substring( ++i)); 
			if( dX >= 0 && dY >= 0 ) return new Point2D.Double( dX, dY);
		}
		return null;
	}
	
	/**
	 * Gets the as xy.
	 * 
	 * @param P the p
	 * 
	 * @return the as xy
	 */
	public static final String getAsXY( Point2D P) 
	{
		return getAsXY( P, 1);
	}
	public static final String getAsXY( Point2D P, int iP) 
	{
		return (P!=null)? GlobalData.DoubleToString( P.getX(), iP) + " " + GlobalData.DoubleToString( P.getY(), iP): "-";
	}
	public static final String getAsXY_2( Point2D P) 
	{
		return (P!=null)? GlobalData.DoubleToString( P.getX(), 2) + " " + GlobalData.DoubleToString( P.getY(), 2): "-";
	}
	
	/**
	 * Gets the coord from xy.
	 * 
	 * @param sCoord the s coord
	 * 
	 * @return the coord from xy
	 */
	public static final Point2D getCoordFromXY( String sCoord) 
	{
		int i = sCoord.indexOf( ' ');
		if( i > 0 ){
			int j = sCoord.indexOf( ';');
			try {
				double dX = Double.parseDouble( sCoord.substring( 0, i)); 
				double dY = Double.parseDouble(  (j>0)? sCoord.substring( ++i, j): sCoord.substring( ++i));
				return new Point2D.Double( dX, dY);
			} catch(  Exception e) {
				return getCoordFromLL( sCoord);
			
			
			} 
		}
		return null;
	}
	public static final Point2D getCoordFromXY( String sCoordX, String sCoordY) 
	{
		try {
			double dX = Double.parseDouble( sCoordX); 
			double dY = Double.parseDouble( sCoordY);
			return new Point2D.Double( dX, dY);
		} catch ( Exception e) {
		} 
		return null;
	}
	public static final Point2D.Double getCoordFromYX( String sCoord) 
	{
		int i = sCoord.indexOf( ' ');
		if( i > 0 ){
			try {
				double dY = Double.parseDouble( sCoord.substring( 0, i)); 
				double dX = Double.parseDouble( sCoord.substring( ++i));
				return new Point2D.Double( dX, dY);
			} catch (NumberFormatException e) {
			} 
		}
		return null;
	}
	
	/**
	 * Gets the coords from xy.
	 * 
	 * @param sCoords the s coords
	 * 
	 * @return the coords from xy
	 */
	public static final Point2D [] getCoordsFromXY( String sCoords) 
	{
		if( sCoords != null ){
			String [] saCoords = sCoords.split( ",");
			if( saCoords != null && saCoords.length > 0){
				int n = saCoords.length;
				Point2D [] Ps = new Point2D.Double[ n];
				for( int i = n; --i >= 0;){
					Ps[ i] = getCoordFromXY( saCoords[ i]);
				}
				return Ps;
			}
		}
		return null;
	}
	
	/**
	 * Gets the angle.
	 * 
	 * @param aPs the a ps
	 * @param aPe the a pe
	 * 
	 * @return the angle
	 */
	public static double getDir( Point2D aPs, Point2D aPe)
	{
		double dX = aPe.getX() - aPs.getX();
		double dY = aPe.getY() - aPs.getY();
		try{
			double dDeg = Math.PI / 2 - Math.atan2( dY, dX);
			if( dDeg < - Math.PI ) dDeg += Math.PI * 2;
			return dDeg;
		}catch( Exception aE ){
			if( dY > 0 ) return 0;
			else return Math.PI;
		}
	}
	public static double getAngle( Point2D aPs, Point2D aPe)
	{
		Point2D aCenter = new Point2D.Double( 0, 0);
		double dS = getDir( aCenter, aPs);
		double dE = getDir( aCenter, aPe);
		dS = dE - dS;
		if( dS > Math.PI ) dS -= Math.PI;
		else if( dS < - Math.PI ) dS += Math.PI * 2;
		
		
		return dS;
	}
	
	/**
	 * Checks if is lL.
	 * 
	 * @param aP the a p
	 * 
	 * @return true, if is lL
	 */
	public static boolean isLL( Point2D aP)
	{
		return aP != null && aP.getY() < 90;
	}
	
	/**
	 * Distance.
	 * 
	 * @param aPs the a ps
	 * @param aPe the a pe
	 * 
	 * @return the double
	 */
	public static double distance(  Point2D aPs, Point2D aPe) 
	{
		if( isLL( aPs) && isLL( aPe) ) return distanceLL( aPs, aPe);
		return aPs.distance( aPe);
	}
	
	/**
	 * Distance ll.
	 * 
	 * @param aPs the a ps
	 * @param aPe the a pe
	 * 
	 * @return the double
	 */
	public static double distanceLL(  Point2D aPs, Point2D aPe) 
	{
		double theta = aPs.getX() - aPe.getX();
		double dist = Math.sin( degToRad( aPs.getY())) * Math.sin( degToRad( aPe.getY())) + 
		  	Math.cos( degToRad( aPs.getY())) * Math.cos( degToRad( aPe.getY())) * Math.cos( degToRad( theta));
		return radToDeg( Math.acos( dist)) * 60 * 1.1515 * 1609.344;
	}
	public static double getDeg(  Point2D aPs, Point2D aPe) 
	{
		double dX = aPe.getX() - aPs.getX();
		double dY = aPe.getY() - aPs.getY();
		return Math.atan2( dX, dY) / Math.PI * 180;
	}
	
	/**
	 * Deg to rad.
	 * 
	 * @param deg the deg
	 * 
	 * @return the double
	 */
	public static double degToRad( double deg) 
	{
		return (deg * Math.PI / 180.0);
	}
	
	/**
	 * Rad to deg.
	 * 
	 * @param rad the rad
	 * 
	 * @return the double
	 */
	public static double radToDeg( double rad) 
	{
		return (rad * 180.0 / Math.PI);
	}
	
	/** The origin shift. */
	private static double originShift =  2 * Math.PI * 6378137 / 2.0;
	
	/**
	 * From ll.
	 * 
	 * @param aP the a p
	 * 
	 * @return the point2 d. double
	 */
	public static Point2D.Double fromLL( Point2D.Double aP) 
	{
		double mx = aP.getX() * originShift / 180.0;
		double my = Math.log( Math.tan((90 + aP.getY()) * Math.PI / 360.0 )) / (Math.PI / 180.0);
		my *= originShift / 180.0;
		return new Point2D.Double( mx, my);
	}
	
	/**
	 * To l lold.
	 * 
	 * @param aP the a p
	 * 
	 * @return the point2 d. double
	 */
	public static Point2D.Double toLLold( Point2D.Double aP) 
	{
		double lon = ( aP.getX() / originShift) * 180.0;
		double lat = ( aP.getY() / originShift) * 180.0;
	    lat = 180.0 / Math.PI * (2 * Math.atan( Math.exp( lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new Point2D.Double( lon, lat);
	}
	
	/** The central meridian. */
	private static double centralMeridian = 24;
	
	/** The lambert_sp_2. */
	private static double lambert_sp_2 = 59.33333333333334;
	
	/** The lambert_sp_1. */
	private static double lambert_sp_1 = 58;
	
	/** The lambert_sp_0. */
	private static double lambert_sp_0 = 57.51755393055556 ;
	
	/** The flattering. */
	private static double flattering = 1 / 298.257222101;
	
	/** The false northing. */
	private static double falseNorthing = 6375000;
	
	/** The false easting. */
	private static double falseEasting = 500000;
	
	/** The semi major axis. */
	private static double semiMajorAxis = 6378137;
	
	/** The angle_sp_1. */
	private static double angle_sp_1 = degToRad( lambert_sp_1);
	
	/** The angle_sp_2. */
	private static double angle_sp_2 = degToRad( lambert_sp_2);
	
	/** The angle_sp_0. */
	private static double angle_sp_0 = degToRad( lambert_sp_0);

	/** The cc_e. */
	private static double cc_e = Math.sqrt( (2 - flattering) * flattering);
	
	/** The cc_m1. */
	private static double cc_m1 = Math.cos( angle_sp_1) / Math.sqrt( 1 - Math.pow( cc_e * Math.sin( angle_sp_1), 2));
	
	/** The cc_m2. */
	private static double cc_m2 = Math.cos( angle_sp_2) / Math.sqrt( 1 - Math.pow( cc_e * Math.sin( angle_sp_2), 2));
	
	/** The cc_t0. */
	private static double cc_t0 = Math.tan( Math.PI/4 - angle_sp_0 / 2) / 
		Math.pow( (1 - cc_e * Math.sin( angle_sp_0)) / (1 + cc_e * Math.sin( angle_sp_0)), cc_e / 2); 
	
	/** The cc_t1. */
	private static double cc_t1 = Math.tan( Math.PI/4 - angle_sp_1 / 2) / 
		Math.pow( (1 - cc_e * Math.sin( angle_sp_1)) / (1 + cc_e * Math.sin( angle_sp_1)), cc_e / 2); 
	
	/** The cc_t2. */
	private static double cc_t2 = Math.tan( Math.PI/4 - angle_sp_2 / 2) / 
		Math.pow( (1 - cc_e * Math.sin( angle_sp_2)) / (1 + cc_e * Math.sin( angle_sp_2)), cc_e / 2); 
	
	/** The cc_n. */
	private static double cc_n = Math.log( cc_m1 / cc_m2) / Math.log( cc_t1 / cc_t2);
	
	/** The cc_ f. */
	private static double cc_F = cc_m1 / cc_n / Math.pow( cc_t1, cc_n);
	
	/** The cc_r0. */
	private static double cc_r0 = semiMajorAxis * cc_F * Math.pow( cc_t0, cc_n);

	/**
	 * To ll.
	 * 
	 * @param aP the a p
	 * 
	 * @return the point2 d. double
	 */
	public static Point2D.Double toLL( Point2D.Double aP ) 
	{
		double cc_np = aP.getY() - falseNorthing;
		double cc_ep = aP.getX() - falseEasting;

		double cc_y = Math.atan( cc_ep / ( cc_r0 - cc_np));
		double lon = centralMeridian + radToDeg( cc_y / cc_n);

		double cc_r = Math.signum( cc_n) * Math.sqrt( cc_ep * cc_ep + Math.pow( cc_r0 - cc_np, 2));
		double cc_t = Math.pow( cc_r / ( semiMajorAxis * cc_F), 1 / cc_n);
		double cc_f = Math.PI/2 - Math.atan( cc_t) * 2;
		for( int i = 10; --i >= 0; ){
			double cc_f0 = cc_f;
			cc_f = Math.PI/2 - Math.atan( cc_t * 
					Math.pow( (1 - cc_e * Math.sin( cc_f0)) / (1 + cc_e * Math.sin( cc_f0)), cc_e / 2)) * 2;
			if( Math.abs( cc_f - cc_f0) <= 0.000001) break;
		}
		double lat = radToDeg( cc_f);
		return new Point2D.Double( lon, lat);
    } 

	/** The distance_sp_one. */
	private static double distance_sp_one = Math.log( Math.cos( angle_sp_1)) - Math.log( Math.cos( angle_sp_2));
	
	/** The distance_sp_two. */
	private static double distance_sp_two = Math.log( Math.tan( angle_sp_2/2 + Math.PI/4))	- 
											Math.log( Math.tan( angle_sp_1/2 + Math.PI/4));
	
	/** The lambert_lamn. */
	private static double lambert_lamn = distance_sp_one / distance_sp_two;
	
	/** The lambert_lamf. */
	private static double lambert_lamf = Math.cos( angle_sp_1) * 
						Math.pow( Math.tan( angle_sp_1/2 + Math.PI/4), lambert_lamn) / lambert_lamn;
	
	/** The lambert_lamr0. */
	private static double lambert_lamr0 = lambert_lamf / Math.pow( Math.tan( angle_sp_0/2 + Math.PI/4), lambert_lamn);
	
	
	
	
    /**
     * To l l2.
     * 
     * @param aP the a p
     * 
     * @return the point2 d. double
     */
    public static Point2D.Double toLL2( Point2D.Double aP ) 
    {
    	double r = lambert_lamr0 - aP.getY();
        double lambert_lamr = Math.signum( lambert_lamn) * Math.sqrt( aP.getX() * aP.getX() + r * r);
 //       double lat = 2 / Math.tan( Math.pow( lambert_lamf / lambert_lamr, 1 / lambert_lamn)) - Math.PI/2;
//    	double lambert_lamro = lambert_lamf / Math.pow( Math.tan( degToRad( aP.getY()/2) + Math.PI/4), lambert_lamn);
    	
        double lon = lambert_sp_0 + radToDeg( Math.asin( aP.getX() / lambert_lamr) / lambert_lamn); 
        double lat = radToDeg( (Math.atan( Math.pow( (Math.abs( lambert_lamf)/ lambert_lamr),
                (1. / lambert_lamn))) * 2.0 - Math.PI/2));
        return new Point2D.Double( lon, lat);
    } 

    /**
     * To l l1.
     * 
     * @param aP the a p
     * 
     * @return the point2 d. double
     */
    public static Point2D.Double toLL1( Point2D.Double aP ) 
    {
//    	double angle_f = degToRad( aP.getY());
//    	double lambert_lamr = lambert_lamf / Math.pow( Math.tan( angle_f/2 + Math.PI/4), lambert_lamn);
    	
        double lat = 90.0 - radToDeg( Math.atan2( Math.pow( (1 / Math.abs( lambert_lamf)),
                (1. / lambert_lamn)), 1) * 2.0);
        double lon = radToDeg( 1 / Math.abs( lambert_lamn)) + centralMeridian;
   	
/*    	double x = aP.getX() - 500000;
    	double y = aP.getY() - 6375000;
        double formula_two = Math.atan2( x, y);
        double formula_one = Math.sqrt( x * x + y * y);

        double lon = radToDeg( formula_two / Math.abs( lambert_lamn)) + centralMeridian;
        
        double lat = 90.0 - radToDeg( Math.atan2( Math.pow( (formula_one / Math.abs( lambert_lamf)),
                (1. / lambert_lamn)), 1) * 2.0);
        if( lambert_lamn < 0.0 ) lat *= -1.0; */
        return new Point2D.Double( lon, lat);
    } 
	
	/**
	 * Gets the cross.
	 * 
	 * @param P0s the p0s
	 * @param P0e the p0e
	 * @param P1s the p1s
	 * @param P1e the p1e
	 * @param bPath the b path
	 * 
	 * @return the cross
	 */
	public static Point2D.Double getCross( Point2D P0s, Point2D P0e,  Point2D P1s, Point2D P1e, boolean bPath)
	{
		double l0x = P0e.getX() - P0s.getX(), l0y = P0e.getY() - P0s.getY();
		double l1x = P1e.getX() - P1s.getX(), l1y = P1e.getY() - P1s.getY();
		double a = l0x*l1y - l0y*l1x;           
		if( Math.abs( a) > 0.01 ){
			double dX = P1s.getX() - P0s.getX();
			double dY = P1s.getY() - P0s.getY();
			double b0 = dX*l1y - dY*l1x;
			double l = b0 / a;
			double b1 = dY*l0x - dX*l0y;
			double m = b1 / -a;
			if( bPath ){
				if( !(l >= 0 && l <= 1 && m >= 0 && m <= 1) ) return null;
			}else if( !(l > 0 && l < 1 && m > 0 && m < 1) ) return null;
//			Point2D.Double Pm = new Point2D.Double( P1s.getX() + l1x * m, P1s.getY() + l1y * m);
Log.info( "   found l=" + l + " m=" + m);
			Point2D.Double Pl = new Point2D.Double( P0s.getX() + l0x * l, P0s.getY() + l0y * l);
			return Pl;
		}
//		else
//			a=0;
		return null;
	}
/*	public static Point2D.Double testCross()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 13, 4);
		Point2D.Double P1e = new Point2D.Double( 4, 7);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross1()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 13, 6);
		Point2D.Double P1e = new Point2D.Double( 4, 9);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross2()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 13, 7);
		Point2D.Double P1e = new Point2D.Double( 4, 10);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross3()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 13, 1);
		Point2D.Double P1e = new Point2D.Double( 4, 4);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross4()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 13, 1);
		Point2D.Double P1e = new Point2D.Double( 4, 4.1);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross5()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 18, 4);
		Point2D.Double P1e = new Point2D.Double( 9, 7);
		return getCross( P0s, P0e, P1s, P1e);
	}
	public static Point2D.Double testCross6()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 10, 8);
		Point2D.Double P1s = new Point2D.Double( 18, 4);
		Point2D.Double P1e = new Point2D.Double( 9, 7.4);
		return getCross( P0s, P0e, P1s, P1e);
	}*/
/*	public static Point2D.Double testCross7()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 64, 44);
		Point2D.Double P1s = new Point2D.Double( 1, 2.1);
		Point2D.Double P1e = new Point2D.Double( 10, 8.1);
		return getCross( P0s, P0e, P1s, P1e, false);
	}
	public static Point2D.Double testCross8()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 64, 44);
		Point2D.Double P1s = new Point2D.Double( 1, 2.999);
		Point2D.Double P1e = new Point2D.Double( 10, 8.1);
		return getCross( P0s, P0e, P1s, P1e, false);
	}
	public static Point2D.Double testCross9()
	{
		Point2D.Double P0s = new Point2D.Double( 1, 2);
		Point2D.Double P0e = new Point2D.Double( 64, 44);
		Point2D.Double P1s = new Point2D.Double( 1, 2.101);
		Point2D.Double P1e = new Point2D.Double( 10, 8.1);
		return getCross( P0s, P0e, P1s, P1e, false);
	} */
	/**
 * Gets the cross.
 * 
 * @param P0 the p0
 * @param P1 the p1
 * 
 * @return the cross
 */
public static Point2D.Double getCross( Point2D [] P0, Point2D [] P1)
	{
		Point2D P0s = null;
		for( int i0 = P0.length; --i0 >= 0;  ){
			Point2D P0e = P0[ i0], P1s = null;
			if( P0s != null ) for( int i1 = P1.length; --i1 >= 0;  ){
				Point2D P1e = P1[ i1];
				if( P1s != null ){
					Point2D.Double P = getCross( P0s, P0e, P1s, P1e, true);
					if( P != null ) return P;
				}
				P1s = P1e;
			}
			P0s = P0e; 
		}
		return null;
	}
	
	/**
	 * D length to line.
	 * 
	 * @param Pr the pr
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the double
	 */
	public static double dLengthToLine( Point2D Pr, Point2D Ps, Point2D Pe)
	{
		double lx = Pe.getX() - Ps.getX(), ly = Pe.getY() - Ps.getY();
		double rx = Pr.getX() - Ps.getX(), ry = Pr.getY() - Ps.getY();	
		double sx = Pr.getX() - Pe.getX(), sy = Pr.getY() - Pe.getY();	
		double dLength = -1;;           

		if( lx == 0 ) dLength = (ry*sy<=0)? Math.abs( rx): -1;
		else if( ly == 0 ) dLength = (rx*sx<=0)? Math.abs( ry): -1;
		else{
			sx = rx*lx + ry*ly;
			sy = rx*ly - ry*lx;
			rx = Math.sqrt( lx*lx + ly*ly);
			sx /= rx;	sy /= rx;
			dLength = ( sx >= 0 && sx <= rx )? Math.abs( sy): -1;
		}
		return dLength;
	}
	
	/**
	 * D length to line.
	 * 
	 * @param Pr the pr
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dLengthToLine( Point2D Pr, Point2D[] Points)
	{ 
		double dMinLen = -1;
		if( Points != null ){
			Point2D Ps = null;
			int n = Points.length;
			boolean bFound = false;

			for( int i = 0; i < n; ++i){ 
				Point2D Pe = Points[ i]; ; 
				double dLen = Pe.distance( Pr);
				if( dLen >= 0.0 && ( !bFound || dLen < dMinLen) ){ 
					dMinLen = dLen;
					bFound = true;
				} 
				if( Ps != null ){
					dLen = dLengthToLine( Pr, Ps, Pe);
					if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
						dMinLen = dLen;
						bFound = true;
					} 
				}	
				Ps = Pe;	
			}
		}
		return dMinLen;
	}
	
	/**
	 * D length to line.
	 * 
	 * @param Pr the pr
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dLengthToLine( Point2D Pr, ArrayList<Point2D> Points)
	{ 
		double dMinLen = -1;
		if( Points != null ){
			Point2D Ps = null;
			int n = Points.size();
			boolean bFound = false;

			for( int i = 0; i < n; ++i){ 
				Point2D Pe = ( Point2D)Points.get( i); ; 
				double dLen = Pe.distance( Pr);
				if( dLen >= 0.0 && ( !bFound || dLen < dMinLen) ){ 
					dMinLen = dLen;
					bFound = true;
				} 
				if( Ps != null ){
					dLen = dLengthToLine( Pr, Ps, Pe);
					if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
						dMinLen = dLen;
						bFound = true;
					} 
				}	
				Ps = Pe;	
			}
		}
		return dMinLen;
	}
	
	/**
	 * Gets the length.
	 * 
	 * @param Points the points
	 * 
	 * @return the length
	 */
	public static double getLength( ArrayList<Point2D> Points)
	{ 
		double dLength = 0;
		if( Points != null ){
			Point2D Ps = null;
			for( int i = Points.size(); --i >= 0;){ 
				Point2D Pe = ( Point2D)Points.get( i); 
				if( Pe != null ){
					if( Ps != null ) dLength += Ps.distance( Pe);
					Ps = Pe;	
				}
			}
		}
		return dLength;
	}
	
	/**
	 * To sting.
	 * 
	 * @param aCoord the a coord
	 * 
	 * @return the string
	 */
	public static String toString( Point2D aCoord, int iFormat)
	{
		StringBuffer Coord = new StringBuffer();
		toString( Coord, aCoord, iFormat);
		return Coord.toString();
	}
	public static String toString( Point2D aCoord)
	{
		StringBuffer Coord = new StringBuffer();
		toString( Coord, aCoord);
		return Coord.toString();
	}
	
	/**
	 * To string.
	 * 
	 * @param Coord the coord
	 * @param aCoord the a coord
	 */
	public static void toString( StringBuffer Coord, Point2D aCoord, int iFormat)
	{
		Coord.append( GlobalData.DoubleToString( aCoord.getX(), iFormat));
		Coord.append( " ");
		Coord.append( GlobalData.DoubleToString( aCoord.getY(), iFormat));
	}
	public static void toString( StringBuffer Coord, Point2D aCoord)
	{
		if( isLL( aCoord) ){
			toString( Coord, aCoord, 6);
/*			Coord.append( GlobalData.DoubleToString( aCoord.getX(), 6));
			Coord.append( " ");
			Coord.append( GlobalData.DoubleToString( aCoord.getY(), 6));*/
		}else{
			toString( Coord, aCoord, 1 );
/*			Coord.append( GlobalData.DoubleToString( aCoord.getX(), 1));
			Coord.append( " ");
			Coord.append( GlobalData.DoubleToString( aCoord.getY(), 1)); */
		}
	}
	public static Point2D [] getPoints( ArrayList<Point2D> aPs)
	{	
		Point2D [] aDPs = null;
		if( aPs != null){
			int n = aPs.size();
			if( n > 0){
				aDPs = new Point2D[ n];
				for( int i = n; --i >= 0; ) aDPs[ i] = (( Point2D)aPs.get( i));
			}
		}
		return aDPs;
	}
	public static Rectangle2D getBBox( Point2D[] Ps) 
	{
		if( Ps == null ) return null;
		int n = Ps.length;
		if( n <= 1 ) return null;
		double dXmin = 0, dXmax = 0;
		double dYmin = 0, dYmax = 0;
		for( int i = 0; i < n; ++i){
			if( i == 0 ){
				dXmin = dXmax = Ps[i].getX();
				dYmin = dYmax = Ps[i].getY();
			}else{
				if( Ps[i].getX() < dXmin ) dXmin = Ps[i].getX();
				else if( Ps[i].getX() > dXmax ) dXmax = Ps[i].getX();
				if( Ps[i].getY() < dYmin ) dYmin = Ps[i].getY();
				else if( Ps[i].getY() > dYmax ) dYmax = Ps[i].getY();
			}
		}
		return new Rectangle2D.Double( dXmin, dYmin, dXmax - dXmin, dYmax - dYmin);
	}
	public static Rectangle2D getBBox( ArrayList<Point2D> aPs) 
	{
		if( aPs == null ) return null;
		int n = aPs.size();
		if( n <= 1 ) return null;
		double dXmin = 0, dXmax = 0;
		double dYmin = 0, dYmax = 0;
		for( int i = 0; i < n; ++i){
			if( i == 0 ){
				dXmin = dXmax = aPs.get( i).getX();
				dYmin = dYmax = aPs.get( i).getY();
			}else{
				if( aPs.get( i).getX() < dXmin ) dXmin = aPs.get( i).getX();
				else if( aPs.get( i).getX() > dXmax ) dXmax = aPs.get( i).getX();
				if( aPs.get( i).getY() < dYmin ) dYmin = aPs.get( i).getY();
				else if( aPs.get( i).getY() > dYmax ) dYmax = aPs.get( i).getY();
			}
		}
		return new Rectangle2D.Double( dXmin, dYmin, dXmax - dXmin, dYmax - dYmin);
	}
	public static Rectangle2D getBBox( Rectangle2D aBBox, ArrayList<Point2D> aPs) 
	{
		if( aPs == null ) return aBBox;
		int n = aPs.size();
		if( n < 2 && aBBox == null ) return null;
		double dXmin = 0, dXmax = 0;
		double dYmin = 0, dYmax = 0;
		if( aBBox != null ){
			dXmin = aBBox.getMinX();
			dXmax = aBBox.getMaxX();
			dYmin = aBBox.getMinY();
			dYmax = aBBox.getMaxY();
		}
		for( int i = 0; i < n; ++i){
			if( i == 0 && aBBox == null ){
				dXmin = dXmax = aPs.get( i).getX();
				dYmin = dYmax = aPs.get( i).getY();
			}else{
				if( aPs.get( i).getX() < dXmin )
					dXmin = aPs.get( i).getX();
				else if( aPs.get( i).getX() > dXmax )
					dXmax = aPs.get( i).getX();
				if( aPs.get( i).getY() < dYmin )
					dYmin = aPs.get( i).getY();
				else if( aPs.get( i).getY() > dYmax )
					dYmax = aPs.get( i).getY();
			}
		}
		return new Rectangle2D.Double( dXmin, dYmin, dXmax - dXmin, dYmax - dYmin);
	}
	public static Point2D getCrossPoint( Point2D aP0, Point2D aP1, Point2D aR0, Point2D aR1)
	{
		double dL;
		double dLx = aP1.getX() - aP0.getX(), dLy = aP1.getY() - aP0.getY();
		if( dLy == 0 ) dL = Math.abs( dLx);
		else if( dLx == 0 ) dL = Math.abs( dLy);
		else dL = Math.sqrt( dLx*dLx + dLy*dLy);
		
		double dMx = aR1.getX() - aR0.getX(), dMy = aR1.getY() - aR0.getY();
		double d = dMy * dLx - dMx * dLy;
		if( d != 0 ){
			double dl = (( aR0.getX() - aP0.getX())*dMy - ( aR0.getY() - aP0.getY())*dMx) / d;
			if( dl >= 0 && dl <= 1 ){
				double dX = dLx * dl + aP0.getX();
				double dY = dLy * dl + aP0.getY();

				double dm = -1;
				if( dMy != 0 ){
					dm = ( dY - aR0.getY()) / dMy;
				}else if( dMx != 0){
					dm = ( dX - aR0.getX()) / dMx;
				}
				if( dm >= 0 && dm <= 1 ){
					return new Point2D.Double( dX, dY);
				}
				if( dL > 0 ){
					
				}
			}
		}
		return null;
	}
	public static Point2D getCrossPoint( Point2D aP0, Point2D aP1, Rectangle2D aRect)
	{
		Point2D aR0 = new Point2D.Double( aRect.getMinX(), aRect.getMinY());
		Point2D aR1 = new Point2D.Double( aRect.getMinX(), aRect.getMaxY());
		Point2D aCP0 = getCrossPoint( aP0, aP1, aR0, aR1);
		aR0 = aR1;
		aR1 = new Point2D.Double( aRect.getMaxX(), aRect.getMaxY()); 
		Point2D aCP1 = getCrossPoint( aP0, aP1, aR0, aR1);
		if( aCP0 == null ){ aCP0 = aCP1; aCP1 = null;}
		if( aCP1 == null){
			aR0 = aR1;
			aR1 = new Point2D.Double( aRect.getMaxX(), aRect.getMinY()); 
			aCP1 = getCrossPoint( aP0, aP1, aR0, aR1);
			if( aCP0 == null ){ aCP0 = aCP1; aCP1 = null;}
			if( aCP1 == null){
				aR0 = aR1;
				aR1 = new Point2D.Double( aRect.getMinX(), aRect.getMinY()); 
				aCP1 = getCrossPoint( aP0, aP1, aR0, aR1);
			}
		}
		if( aCP0 == null ) return aCP1;
		else if( aCP1 == null ) return aCP0;
		return ( distance( aP0, aCP0) < distance( aP0, aCP1))? aCP0: aCP1;
	}
	public static double getLineLength( Point2D[] Points)
	{ 
		Point2D aPs = null;
		double dLength = 0.0;
		for( int i = Points.length;  --i >= 0;  ){ 
			Point2D aPe = Points[ i]; 
			if( aPs != null ) dLength += aPs.distance( aPe);
			aPs = aPe;	
		}
		return dLength;
	}
	public static Point2D getLineProjPoint( Point2D Pr, Point2D Ps, Point2D Pe)
	{
		double lx = Pe.getX() - Ps.getX(), ly = Pe.getY() - Ps.getY();
		if( lx != 0 && ly != 0){
			double dL = lx*lx + ly*ly;
			double rx = Pr.getX() - Ps.getX(), ry = Pr.getY() - Ps.getY();	
			double l = rx*lx + ry*ly;
			l /= dL;	
			if( l >= 0 && l <= 1 ) return new Point2D.Double( Ps.getX() + lx * l, Ps.getY() + ly * l);
		}
		return null;
	}
	public static int getNearestPoint( Point2D aPr, Point2D [] Path)
	{
// erineb eelmisest selle poolest, et vaatab ka k��nakute endi kaugusi
// saab alati vastuse kui on v�hemalt �ks punkt	
		int iMin = -1;
		int n = Path.length;
		if( n > 0 ){
			double dMinL = 0;
			for( int i = n; --i >= 0;){
				Point2D aP = Path[ i];
				if( aP != null ){
					double dL = aP.distance( aPr); 
					if( dL >= 0 && (iMin < 0 || dL < dMinL) ){
						iMin = i;
						dMinL = dL;
					}	 
				}
			}
			Point2D aP = Path[ iMin];
			Point2D aP0 = ( iMin > 0)? GlobalGIS.getLineProjPoint( aPr, Path[ iMin-1], aP): null;
			Point2D aP1 = ( iMin+1 < n)? GlobalGIS.getLineProjPoint( aPr, aP, Path[ iMin+1]): null;
			if( aP0 != null && aP1 != null){ // kui m�lemad eksisteerivad, siis kumb on parem
				double dL0 = aP0.distance( aPr);
				double dL1 = aP1.distance( aPr);
				if( dL0 < dL1 )	aP1 = null; else aP0 = null;
			}
			if( aP0 != null ){
				aPr.setLocation( aP0);
				--iMin;
			}else if( aP1 != null ){
				aPr.setLocation( aP1);
			}else{
				aPr.setLocation( aP);
				iMin = -( ++iMin);
			}
		}
		return iMin;
	}
	public static double getArea( Point2D PM[])
	{
		double	dArea = 0.0;
		int	iM;
		int nPoints = PM.length;
	
		if( nPoints < 3 ) dArea = 0.0;
		else if( nPoints == 3 )
			dArea = Math.abs( ((PM[1].getX() - PM[0].getX()) * (PM[2].getY()-PM[0].getY()) -
				(PM[2].getX() - PM[0].getX())*(PM[1].getY() - PM[0].getY()))*0.5);
		else{
			int	i;
			double	dA;

/* for( i = 0; i <= nPoints; ++i){
	char buf[80];
	sprintf( buf, "Point[%d] %lf %lf", i, Points[i].x, Points[i].y);
	MsgBox( buf);
}*/
			Point2D Points[] = new Point2D.Double[ nPoints+1];
			for( iM = nPoints; --iM >= 0; ) Points[ iM] = PM[ iM];
			Points[ nPoints] = PM[0];

			for( dA = 0.0, i = 0; i < nPoints-1; ++i){
				dA += AngleDPoly( Points, i);
			}
			for( ;;){
			for( i = nPoints-1; --i >= 0; ){
				double	dP;
	
				dP = AngleDPoly( Points, i);
				if( dP * dA > 0 ){	// nurk oiget pidi
					double	dX1, dX2, dY1, dY2, dL; //, dM;
					int	j;

					dX2 = Points[i+2].getX() - Points[i].getX();
					dY2 = Points[i+2].getY() - Points[i].getY();
		  //		dM = sqrt( dX2*dX2 + dY2*dY2);
					dX1 = Points[i+1].getX() - Points[i].getX();
					dY1 = Points[i+1].getY() - Points[i].getY();
					dL = dX1*dY2 - dY1*dX2;

					for( j = nPoints;  --j >= 0;){ // kontrollida kas on kolmnurga sees
						Point2D	P[] = new Point2D.Double[3];
						double	dPx;

						if( j >= i && j <= i+2 ) continue;

						P[0] = Points[i];
						P[1] = Points[i+1];
						P[2] = Points[j];
						dPx = AngleDPoly( P, 0);           // on kolmnurga sektoris
						if( dPx * dP > 0 && Math.abs(dPx) > Math.abs( dP) ){
							double	dLx;

							dLx = (Points[j].getX() - Points[i].getX())*dY2 -
								(Points[j].getY() - Points[i].getY())*dX2;
/* {
char buf[80];
sprintf( buf, "Sektoris %lf %lf  %lf %lf", dLx, dL, dP, dPx);
MsgBox( buf);
} */
							if( dLx * dL > 0 && Math.abs(dLx) < Math.abs( dL) ) break;
					}	}
					if( j < 0 ){	// voib kolmnurga �raloigata
						dArea += Math.abs( dL / 2); // AreaDPoly
						for( iM = i; ++iM < nPoints;) Points[iM] = Points[iM+1];
						--nPoints;
						break;
					} // else MsgBox( "Imekujund");
			}  }
			if( i >= 0 ){
				if( nPoints <= 3 ){
					dArea += Math.abs( ((Points[1].getX()-Points[0].getX())*(Points[2].getY()-Points[0].getY()) -
						(Points[2].getX() - Points[0].getX())*(Points[1].getY() - Points[0].getY()))*0.5);
					break;
				}
			}else{
//				MsgBox( "Puudub pindala");
				break;
			}
			}
/* for( i = 0; i <= nPoints; ++i){
	char buf[80];
	sprintf( buf, "Point[%d] %lf %lf", i, Points[i].x, Points[i].y);
	MsgBox( buf);
}  } */

		}
//	if( Points != PM ) free( Points);
		return dArea;
	}
	public static double AngleDPoly( Point2D Points[], int i)
	{
		double	d, dX1, dY1, dX3, dY3, dX, dY;

		dX3 = Points[i+2].getX() - Points[i+1].getX();
		dY3 = Points[i+2].getY() - Points[i+1].getY();
		dX1 = Points[i+1].getX() - Points[i].getX();
		dY1 = Points[i+1].getY() - Points[i].getY();
		dX = dX1*dX3 + dY1*dY3;
		dY = dY3*dX1 - dY1*dX3;
		if( dX == 0.0 ) d = ((dY<0.0)? -Math.PI: Math.PI) / 2;
		else if( dY == 0.0 ) d = 0.0;
		else{
			d = Math.atan( dY / dX);
			if( dX < 0.0 ){
				if( d < 0.0 ) d += Math.PI;
				else if( d > 0.0 ) d -= Math.PI;
		}	}
		return d;
	}

	public static void main1(String[] args) 
	{
		Point2D aCP;
		try{
			Point2D.Double aP1 = new Point2D.Double( -4, 0);
			Point2D.Double aP0 = new Point2D.Double( -3, 4);
			Point2D.Double aR1 = new Point2D.Double( 0, -4);
			Point2D.Double aR0 = new Point2D.Double( 6, 6);
			aCP = getCrossPoint( aP0, aP1, aR0, aR1);
			if( aCP != null) return;
		}catch( Exception aE ){
			aE.printStackTrace();
		}
	}
	public static void main2( String[] args) 
	{
		Point2D aCP;
		try{
			Point2D.Double aP1 = new Point2D.Double( -4, 0);
			Point2D.Double aP0 = new Point2D.Double( -2, 4);
			Rectangle2D.Double aRect = new Rectangle2D.Double( -3, -3, 6, 6);
			aCP = getCrossPoint( aP0, aP1, aRect);
			if( aCP != null) return;
		}catch( Exception aE ){
			aE.printStackTrace();
		}
	}
/*	public static void main( String[] args) 
	{
		Point2D aCP;
		try{
			Point2D.Double aPe = new Point2D.Double( -3, -2);
			Point2D.Double aPs = new Point2D.Double( -8, -7);
			Point2D.Double aPr = new Point2D.Double( -11, -4);
			Point2D.Double aP = getLineProjPoint( aPr, aPs, aPe);
			return;
		}catch( Exception aE ){
			aE.printStackTrace();
		}
	}*/
	public static String getAreaString( double dArea)
	{
		long lArea = Math.round( dArea);
		if( lArea >= 10000 ){
			lArea = Math.round( dArea/10000);
			if( lArea >= 1000 ){
				return GlobalData.DoubleToString( dArea/1000000, 1) + " km�"; 
			}
			if( lArea >= 50 ) return GlobalData.DoubleToString( dArea/10000, 1) + " ha";
			return GlobalData.DoubleToString( dArea/10000, 2) + " ha";
		}		
		return lArea + " m�";
	}
/*	public static double getLengthToProj( Point2D[] Points, Point2D Pr)
	{ 
		double dMinLen = -1, dLen;
		Point2D Ps = null, Pe = null;
		int n = Points.length;
		int iMin = -1;
//		double dLength = 0.0;
		double dProjLength = -1;

		for( int i = 0; i < n; ++i ){ 
			dLen = Pr.distance( Points[ i]);
			if( dLen >= 0.0 && ( iMin < 0 || dLen < dMinLen) ){ 
				dMinLen = dLen;
				iMin = i;
			}
			Ps = Pe;	
		}
		if( iMin >= 0 ){
			double dP1 = -1; 
			if( iMin > 0 ){
/*				Ps = new Point2D.Double( Points[ iMin-2], Points[ iMin-1]); 
				Pe = new Point2D.Double( Points[ iMin], Points[ iMin+1]); 
				dP1 = dLengthToProj( Pr, Ps, Pe); 
			}
			if( iMin < n-2 ){
				Ps = new Point2D.Double( Points[ iMin], Points[ iMin+1]); 
				Pe = new Point2D.Double( Points[ iMin+2], Points[ iMin+3]); 
				double dP2 = dLengthToProj( Pr, Ps, Pe); 
				if( dP2 > 0 ){
					if( dP1 < 0 || dP2 < dP1 ) dProjLength = dP2;
					else{
						 dProjLength = dP1;
						 iMin -= 2;
					}
				}
			}else if( dP1 >= 0 ){
				 dProjLength = dP1;
				 iMin -= 2;
			} 
			if( iMin > 0 ) dProjLength += getLengthTo( Points, iMin/2+1);
		}			
		return dProjLength;
	}*/
	public static double getLengthTo( double[] Points, int n)
	{ 
		Point2D.Double Ps = null, Pe = null;
		double dLength = 0.0;

		for( int i = 0; i < n*2;  ){ 
			Pe = new Point2D.Double( Points[ i++], Points[ i++]); 
			if( Ps != null ){
				dLength += GlobalFunc.dLineLength( Ps,Pe);
			}
			Ps = Pe;	
		}
		return dLength;
	}

}

