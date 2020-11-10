/*
 * 
 */
package ee.or.is;

import java.nio.ByteBuffer;
import java.awt.Point;
import java.awt.geom.*;
// import java.sql.Date;
import java.util.*;
import java.sql.Date;
import java.text.*;
// TODO: Auto-generated Javadoc
//import	com.ms.wfc.io.*;
//import java.io.*;
//import java.net.*;
//import java.io.*;

/**
 * The Class GlobalFunc.
 */
public abstract class GlobalFunc
{
	
	/** The i marker size. */
	public static int iMarkerSize;
	
	/**
	 * Sets the marker size.
	 * 
	 * @param iNewSize the new marker size
	 */
	public static void setMarkerSize( int iNewSize)
	{
		iMarkerSize = iNewSize;
	}
	
	/**
	 * Gets the marker size.
	 * 
	 * @return the marker size
	 */
	public static int getMarkerSize()
	{
		return iMarkerSize;
	}
	
	/**
	 * Round to long.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the long
	 */
	public static long RoundToLong( double dValue)
	{
		return ( long)((dValue >= 0)? dValue + 0.5: dValue - 0.5);
	}
	
	/**
	 * Round to int.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the int
	 */
	public static int RoundToInt( double dValue)
	{
		return ( int)((dValue >= 0)? dValue + 0.5: dValue - 0.5);
	}
/*	public unsigned int RoundToUInt( double dValue)
	{
		if( (dValue += 0.5) >= 0 ) return ( unsigned int)dValue;
		return UINT_MAX;
	} */
	/**
 * Round to u short.
 * 
 * @param dValue the d value
 * 
 * @return the char
 */
public static char RoundToUShort( double dValue)
	{
		int i = ( int)((dValue >= 0)? dValue + 0.5: dValue - 0.5); 
		
		if( i < 0 || i > 0xFFFF ) i = 0xFFFF;
		return ( char)i;
	}
	
	/**
	 * Round to short.
	 * 
	 * @param dValue the d value
	 * 
	 * @return the short
	 */
	public static short RoundToShort( double dValue)
	{
		int i = ( int)((dValue >= 0)? dValue + 0.5: dValue - 0.5); 

		if( -i >= 0x7FFF || i > 0x7FFF ) i = 0x7FFF;
		return ( short)i;
	}
	
	/**
	 * Bytes to u byte.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the char
	 */
	public static char BytesToUByte( byte Par[], int iIndex)
	{
		return ( char)( ( Par[ iIndex] < 0 )? (0x100 + Par[ iIndex]): Par[ iIndex]);
	}
/*	public static byte BytesToByte( byte Par[], int iIndex)
	{
		return Par[ iIndex];
	}
*/	
	/**
 * Double to string.
 * 
 * @param d the d
 * @param iP the i p
 * 
 * @return the string
 */
public static String DoubleToString( double d, int iP) 
	{
		String sBuf = new String("");
		int	i, iDec = 1, iLen;
		for( i = iP; --i >= 0;) iDec *= 10;
		
		i = RoundToInt( d*iDec);
		sBuf = String.valueOf( i);
		if( iP > 0 ){
			if( (iLen = sBuf.length()) > iP ) 
				sBuf = sBuf.substring( 0, iLen - iP) + "." + sBuf.substring( iLen - iP);
			else{ 
				String sBuf1 = "0.";
				for( i = iP-iLen; --i >= 0; ) sBuf1 += "0";
				sBuf = sBuf1 + sBuf;
			}
		}
		return sBuf;
	}
	
	/**
	 * Int to string.
	 * 
	 * @param iValue the i value
	 * 
	 * @return the string
	 */
	public static String IntToString( int iValue) 
	{
		return Integer.toString( iValue);
	}  
	
	/**
	 * Int to string.
	 * 
	 * @param iValue the i value
	 * @param iLen the i len
	 * 
	 * @return the string
	 */
	public static String IntToString( int iValue, int iLen) 
	{
		String	sValue = IntToString( iValue); 
		int		n = (sValue!=null)? sValue.length(): 0;
		if( n > 0 && n < iLen ){
			StringBuffer sBuf = new StringBuffer();
			for( int i = iLen-n;  --i >= 0; ) sBuf.append( ' ');
			sBuf.append( sValue);
			return sBuf.toString();
		}
		return sValue;
	}  
	
	/**
	 * Int to string zero.
	 * 
	 * @param iValue the i value
	 * @param iLen the i len
	 * 
	 * @return the string
	 */
	public static String IntToStringZero( int iValue, int iLen) 
	{
		String	sValue = IntToString( iValue); 
		int		n = (sValue!=null)? sValue.length(): 0;
		if( n > 0 && n < iLen ){
			StringBuffer sBuf = new StringBuffer();
			for( int i = iLen-n;  --i >= 0; ) sBuf.append( '0');
			sBuf.append( sValue);
			return sBuf.toString();
		}
		return sValue;
	}  
	/* public static short BytesToShort( byte Par[], int iIndex)
	{
		short s;
	
		s = Par[ 1];
		if( s < 0 ) s = ( short)0x80 - s;
		s *= 0x100;
		if( Par[0] < 0 ) s += (0x80 - Par[ 0]);
		else s += Par[0];
		return s;
	}*/
	/**
	 * Bytes to string.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * @param iLength the i length
	 * 
	 * @return the string
	 */
	public static String BytesToString( byte Par[], int iIndex, int iLength)
	{
//		MemoryStream MS = new MemoryStream( Par, iIndex, Par.length);
		return new String( Par, iIndex, iLength);
	}
	
	/**
	 * Bytes to char.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the char
	 */
	public static char BytesToChar( byte Par[], int iIndex)
	{
//		MemoryStream MS = new MemoryStream( Par, iIndex, Par.length);
		return ( char)Par[ iIndex];
	}
    
    /**
     * Signed to int.
     * 
     * @param b the b
     * 
     * @return the int
     */
    public static final int signedToInt(byte b) {
		return ((int)b & 0xff);
    }
	
	/**
	 * Bytes to short.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the short
	 */
	public static short BytesToShort( byte Par[], int iIndex)
	{
//		MemoryStream MS = new MemoryStream( Par, iIndex, Par.length);
		return (short)(((int)( Par[ iIndex+1]) << 8) | signedToInt( Par[ iIndex]));
	}
	
	/**
	 * Bytes to u short.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the char
	 */
	public static char BytesToUShort( byte Par[], int iIndex)
	{
//		MemoryStream MS = new MemoryStream( Par, iIndex, Par.length);
		return ( char)((((int)( Par[ iIndex+1]) << 8) | signedToInt( Par[ iIndex])) & 0xFFFF);
	}
	
	/**
	 * Bytes to int.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the int
	 */
	public static int BytesToInt( byte Par[], int iIndex)
	{
		int i = Par[ iIndex+3] << 24;
		i |= signedToInt( Par[ iIndex+2]) << 16;
		i |= signedToInt( Par[ iIndex+1]) << 8;		i |= signedToInt( Par[ iIndex]);		return i;
//		return ((int)( Par[ iIndex+3])) << 24 | signedToInt( Par[ iIndex+2]) << 16 |//			   signedToInt( Par[ iIndex+1]) << 8 | signedToInt( Par[ iIndex]);
	}
	
	/**
	 * Bytes to float.
	 * 
	 * @param Par the par
	 * @param iIndex the i index
	 * 
	 * @return the float
	 */
	public static float BytesToFloat( byte Par[], int iIndex)
	{
		int i = ((int)( Par[ iIndex+3]) << 24) | signedToInt( Par[ iIndex+2]) << 16 |			   signedToInt( Par[ iIndex+1]) << 8 | signedToInt( Par[ iIndex]);
		return Float.intBitsToFloat( i);
	} 
/*	public static void draw_ellipse( int x, int y, int rx, int ry)
{
	draw_ellipseW( x - rx, y - ry, x + rx + 1, y + ry + 1);
} */
/*	public static void drawEllipseDir( Graphics gr, int x, int y, int dir, int rx, int ry){
		int		ix, lx, ly, ix1=0, ix2=0, ix3=0, ix4=0, iy1=0, iy2=0, iy3=0, iy4=0;
		double 	d, s, c, w_asp = 1.0;
		

		d = - dir / 1800.0 * Math.PI;
		s = Math.sin( d);	c = Math.cos( d);
		for( ix = 0; ix <= rx; ++ix){
			if( ix == 0 ) d = ry;
			else if( ix == rx ) d = 0.0;
			else{
				d = ( double)ix / rx;
				d = Math.sqrt( 1.0 - d * d ) * ry;
			}
			lx = x + RoundToInt( c * ix - s * d);
			ly = y + RoundToInt((s * ix + c * d) / w_asp);
			if( ix > 0 ) gr.drawLine( ix1, iy1, lx, ly);
			ix1 = lx;     iy1 = ly;
			lx = x - RoundToInt( c * ix - s * d);
			ly = y - RoundToInt((s * ix + c * d) / w_asp);
			if( ix > 0 ) gr.drawLine( ix2, iy2, lx, ly);
			ix2 = lx;     iy2 = ly;
			lx = x + RoundToInt( c * ix + s * d);
			ly = y + RoundToInt((s * ix - c * d) / w_asp);
			if( ix > 0 ) gr.drawLine( ix3, iy3, lx, ly);
			ix3 = lx;     iy3 = ly;
			lx = x - RoundToInt( c * ix + s * d);
			ly = y - RoundToInt((s * ix - c * d) / w_asp);
			if( ix > 0 ) gr.drawLine( ix4, iy4, lx, ly);
			ix4 = lx;     iy4 = ly;
	}	}
	public static void drawEllipseArcDir( Graphics gr, int x, int y, int dir, int a0, int al, int rx, int ry){
		int		i, ix=0, lx, ly, iy=0, ix0, iiY0, ixl, iyl, ey, k = 0, a, j;
		double 	d, s, c, t, w_asp = 1.0;

		while( a0 < 0 ) a0 += 3600;
		while( al < a0 ) al += 3600;
		j = a0 % 3600;
		if( j == 900 ){ iiY0 = ry; ix0 = 0;}
		else if( j == 2700 ){ iiY0 = -ry; ix0 = 0;}
		else{
			if( rx > 0 ){
				d = a0 / 1800.0 * Math.PI;
				t = Math.tan( d);
				d = ry * w_asp;
				s = d / rx;
				ix0 = RoundToInt( (d *= 1.0 / Math.sqrt( s*s + t*t) ));
				iiY0 = RoundToInt( Math.abs( t * d) / w_asp);
			}else{ ix0 = 0; iiY0 = ry;}
			if( j > 1800 ) iiY0 = - iiY0;
		}
// draw_line( x, y, x + (( j > 900 && j < 2700)? -ix0: ix0), y-iiY0);
		j = al % 3600;
		if(  j == 900 ){ iyl = ry; ixl = 0;}
		else if( j == 2700 ){ iyl = -ry; ixl = 0;}
		else{
			if( rx > 0 ){
				d = al / 1800.0 * Math.PI;
				t = Math.tan( d);
				d = ry * w_asp;
				s = d / rx;
				ixl = RoundToInt( (d *= 1.0 / Math.sqrt( s*s + t*t) ));
//			if( j > 900 && j < 2700 ) ixl = - ixl;
				iyl = RoundToInt( Math.abs( t * d) / w_asp);
			}else{ ixl = 0; iyl = ry;}
			if( j > 1800 ) iyl = - iyl;
		}
		d = dir / 1800.0 * Math.PI;
		s = Math.sin( d);	c = Math.cos( d);
		for( a = 0; a < al; a += 900){
			if( a + 900 <= a0 ) continue;
			j = (a / 900) % 4;
			if( a0 >= a ) i = /* (j>1)? -iiY0:/ iiY0;
			else if( j == 1 ) i = ry;
			else if( j == 3 ) i = -ry;
			else i = 0;
			if( al <= 900 + a ) ey = iyl;
			else if( j == 0 ) ey =  ry;
			else if( j == 2 ) ey = -ry;
			else ey = 0;
//draw_line( x, y, x + (( j > 900 && j < 2700)? -ixl: ixl), y-ey);
//		if( i == ey ){
//			if( ix0 != ixl ) draw_line( ix0, iiY0, ixl, iyl);
//		}else
			for( ;;){
				if( i == iiY0 && a0 >= a ) d = ix0;
				else if( i == iyl && al <= 900 + a ) d = ixl;
				else
				{
					if( i == 0 ) d = rx;
					else if( i == ry ) d = 0.0;
					else{
						d = ( double)i / ry;
						d = Math.sqrt( 1.0 - d * d ) * rx;
				}	}
				if( j == 1 || j == 2 ) d = -d;
				lx = x + RoundToInt( c * d - s * i * w_asp);
				ly = y - RoundToInt( s * d / w_asp + c * i);
// draw_line( x, y, lx, ly);
				if( k == 0 ){ k = 1;	ix = lx; iy = ly;}
				else if( ix != lx || iy != ly ){
					gr.drawLine( ix, iy, lx, ly);
					ix = lx; iy = ly;
				}
				if( i == ey ) break;
				if( ey > i ) ++i;
				else --i;
		} 	}
/*		if( iDrawType & 1 ){
			ix = x + RoundToInt( c * ix0 - s * iiY0 * w_asp);
			iy = y - RoundToInt( s * ix0 / w_asp + c * iiY0);
			lx = x + RoundToInt( c * ixl - s * iyl * w_asp);
			ly = y - RoundToInt( s * ixl /
		} /
	}  
	public static void drawBox( Graphics gr, int x0, int y0, int x1, int y1, RasterOp Op){
		gr.drawLine( x0, y0, x1, y0, Op);
		gr.drawLine( x1, y0, x1, y1, Op);
		gr.drawLine( x1, y1, x0, y1, Op);
		gr.drawLine( x0, y1, x0, y0, Op);
	}
/*	public static Point BShortToPoint( byte Par[], int iIndex)
	{
		Point P = new Point();
		
		P.x = Par[ iIndex];
		if( P.x < 0 ) P.x = 0x007F - P.x;
		P.y = Par[ iIndex+1];
		if( P.y < 0 ) P.y = 0x007F - P.y;
		return P;
	}
	public static Point ShortToPoint( byte Par[], int iIndex)
	{
		Point P = new Point();
		
		P.x = BytesToShort( Par, iIndex);
		if( P.x < 0 ) P.x = 0x7FFF - P.x;
		P.y = BytesToShort( Par, iIndex+2);
		if( P.y < 0 ) P.y = 0x7FFF - P.y;
		return P;
	}
	public static Point IntToPoint( byte Par[], int iIndex)
	{
/*		Point P = new Point();
		
		P.x = BytesToInt( Par, iIndex);
		P.y = BytesToInt( Par, iIndex+4); 
		return P; /
		return new Point( BytesToInt( Par, iIndex), BytesToInt( Par, iIndex+4));
	}
	public static Point SShortToPoint( byte Par[], int iIndex)
	{
/*		Point P = new Point();
		
		P.x = BytesToShort( Par, iIndex);
		P.y = BytesToShort( Par, iIndex+2); /
		return new Point( BytesToShort( Par, iIndex), BytesToShort( Par, iIndex+2));
	}*/
/*	public class PointS{
		short x;
		short y;
		public PointS()
		{
//			x = new short();
//			y = new short();
		}
		public void dispose()
		{
//			x.dispose();
//			y.dispose();
		}
	}
	public static PointS ShortToPointS( byte Par[], int iIndex)
	{
		PointS P = new PointS();
		
		P.x = ShortToShort( Par, iIndex);
		P.y = ShortToShort( Par, iIndex+2);
		return P;
	} /
	public static double AngleDPoly( double dX[], double dY[], int i)
	{
		double	d, dX1, dY1, dX3, dY3, dXk, dYk;

		dX3 = dX[i+2] - dX[i+1];
		dY3 = dY[i+2] - dY[i+1];
		dX1 = dX[i+1] - dX[i];
		dY1 = dY[i+1] - dY[i];
		dXk = dX1*dX3 + dY1*dY3;
		dYk = dY3*dX1 - dY1*dX3;
		if( dXk == 0.0 ) d = ((dYk<0.0)? -Math.PI: Math.PI) / 2;
		else if( dYk == 0.0 ) d = 0.0;
		else{
			d = Math.atan( dYk / dXk);
			if( dXk < 0.0 ){
				if( d < 0.0 ) d += Math.PI;
				else if( d > 0.0 ) d -= Math.PI;
		}	}
		return d;
	}
	public static double AreaDPoly( double dX[], double dY[], int nPoints)
	{
		double	dArea = 0.0;
		int	iM;
	
		if( nPoints < 3 ) dArea = 0.0;
		else if( nPoints == 3 )
			dArea = Math.abs( ((dX[1]-dX[0])*(dY[2]-dY[0]) -
				(dX[2] - dX[0])*(dY[1] - dY[0]))*0.5);
		else{
			int	i;
			double	dA;

/* for( i = 0; i <= nPoints; ++i){
	char buf[80];
	sprintf( buf, "Point[%d] %lf %lf", i, Points[i].x, Points[i].y);
	MsgBox( buf);
} /
			double dXn[] = new double[nPoints+1];
			double dYn[] = new double[nPoints+1];
//			Point Points[] = new Point[ nPoints+1];
			for( iM = nPoints; --iM >= 0; ){
				// Points[ iM] = PM[ iM];
				dXn[ iM] = dX[ iM];
				dYn[ iM] = dY[ iM];
			}
//			Points[ nPoints] = PM[0];
			dXn[ nPoints] = dX[ 0];
			dYn[ nPoints] = dY[ 0];
			

			for( dA = 0.0, i = 0; i < nPoints-1; ++i){
				dA += AngleDPoly( dXn, dYn, i);
			}
			for( ;;){
			for( i = nPoints-1; --i >= 0; ){
				double	dP;
	
				dP = AngleDPoly( dXn, dYn, i);
				if( dP * dA > 0 ){	// nurk oiget pidi
					double	dX1, dX2, dY1, dY2, dL; //, dM;
					int	j;

					dX2 = dXn[i+2] - dXn[i];
					dY2 = dYn[i+2] - dYn[i];
		  //		dM = sqrt( dX2*dX2 + dY2*dY2);
					dX1 = dXn[i+1] - dXn[i];
					dY1 = dYn[i+1] - dYn[i];
					dL = dX1*dY2 - dY1*dX2;

					for( j = nPoints;  --j >= 0;){ // kontrollida kas on kolmnurga sees
//						Point	P[] = new Point[3];
						double	dXu[] = new double[ 3];
						double	dYu[] = new double[ 3];
						double	dPx;

						if( j >= i && j <= i+2 ) continue;

						dXu[0] = dXn[i];
						dYu[0] = dYn[i];
						dXu[1] = dXn[i+1];
						dYu[1] = dYn[i+1];
						dXu[2] = dXn[j];
						dYu[2] = dYn[j];
						dPx = AngleDPoly( dXu, dYu, 0);           // on kolmnurga sektoris
						if( dPx * dP > 0 && Math.abs(dPx) > Math.abs( dP) ){
							double	dLx;

							dLx = (dXn[j] - dXn[i])*dY2 - (dYn[j] - dYn[i])*dX2;
/* {
char buf[80];
sprintf( buf, "Sektoris %lf %lf  %lf %lf", dLx, dL, dP, dPx);
MsgBox( buf);
} /
							if( dLx * dL > 0 && Math.abs(dLx) < Math.abs( dL) ) break;
					}	}
					if( j < 0 ){	// voib kolmnurga äraloigata
						dArea += Math.abs( dL / 2); // AreaDPoly
						for( iM = i; ++iM < nPoints;){ // Points[iM] = Points[iM+1];
							dXn[ iM] = dXn[ iM+1];
							dYn[ iM] = dYn[ iM+1];
						}
						--nPoints;
						break;
					} // else MsgBox( "Imekujund");
			}  }
			if( i >= 0 ){
				if( nPoints <= 3 ){
					dArea += Math.abs( ((dXn[1] - dXn[0])*(dYn[2] - dYn[0]) -
						(dXn[2] - dXn[0])*(dYn[1] - dYn[0]))*0.5);
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
}  }  /

		}
//	if( Points != PM ) free( Points);
		return dArea;
	}
	
/*	public static void drawLines( Graphics gr, Point pDraw[]){
		int i;
		int	ix, iy, jx = 0, jy = 0;
		boolean	bFirst = false;
		
		for( i = pDraw.length; --i >= 0;){
			ix = pDraw[ i].x;
			iy = pDraw[ i].y;
			if( bFirst ) gr.drawLine( ix, iy, jx, jy);
			else bFirst = true;
			jx = ix; jy = iy;
		}
	} */
	/**
 * Angle d poly.
 * 
 * @param Points the points
 * @param i the i
 * 
 * @return the double
 */
public static double AngleDPoly( Point2D.Double Points[], int i)
	{
		double	d, dX1, dY1, dX3, dY3, dX, dY;

		dX3 = Points[i+2].x - Points[i+1].x;
		dY3 = Points[i+2].y - Points[i+1].y;
		dX1 = Points[i+1].x - Points[i].x;
		dY1 = Points[i+1].y - Points[i].y;
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
	
	/**
	 * Gets the area.
	 * 
	 * @param PM the pM
	 * 
	 * @return the area
	 */
	public static double getArea( Point2D.Double PM[])
	{
		double	dArea = 0.0;
		int	iM;
		int nPoints = PM.length;
	
		if( nPoints < 3 ) dArea = 0.0;
		else if( nPoints == 3 )
			dArea = Math.abs( ((PM[1].x-PM[0].x)*(PM[2].y-PM[0].y) -
				(PM[2].x - PM[0].x)*(PM[1].y - PM[0].y))*0.5);
		else{
			int	i;
			double	dA;

/* for( i = 0; i <= nPoints; ++i){
	char buf[80];
	sprintf( buf, "Point[%d] %lf %lf", i, Points[i].x, Points[i].y);
	MsgBox( buf);
}*/
			Point2D.Double Points[] = new Point2D.Double[ nPoints+1];
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

					dX2 = Points[i+2].x - Points[i].x;
					dY2 = Points[i+2].y - Points[i].y;
		  //		dM = sqrt( dX2*dX2 + dY2*dY2);
					dX1 = Points[i+1].x - Points[i].x;
					dY1 = Points[i+1].y - Points[i].y;
					dL = dX1*dY2 - dY1*dX2;

					for( j = nPoints;  --j >= 0;){ // kontrollida kas on kolmnurga sees
						Point2D.Double	P[] = new Point2D.Double[3];
						double	dPx;

						if( j >= i && j <= i+2 ) continue;

						P[0] = Points[i];
						P[1] = Points[i+1];
						P[2] = Points[j];
						dPx = AngleDPoly( P, 0);           // on kolmnurga sektoris
						if( dPx * dP > 0 && Math.abs(dPx) > Math.abs( dP) ){
							double	dLx;

							dLx = (Points[j].x - Points[i].x)*dY2 -
								(Points[j].y - Points[i].y)*dX2;
/* {
char buf[80];
sprintf( buf, "Sektoris %lf %lf  %lf %lf", dLx, dL, dP, dPx);
MsgBox( buf);
} */
							if( dLx * dL > 0 && Math.abs(dLx) < Math.abs( dL) ) break;
					}	}
					if( j < 0 ){	// voib kolmnurga äraloigata
						dArea += Math.abs( dL / 2); // AreaDPoly
						for( iM = i; ++iM < nPoints;) Points[iM] = Points[iM+1];
						--nPoints;
						break;
					} // else MsgBox( "Imekujund");
			}  }
			if( i >= 0 ){
				if( nPoints <= 3 ){
					dArea += Math.abs( ((Points[1].x-Points[0].x)*(Points[2].y-Points[0].y) -
						(Points[2].x - Points[0].x)*(Points[1].y - Points[0].y))*0.5);
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
	
	/**
	 * Checks if is inside poly.
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param P the p
	 * 
	 * @return true, if is inside poly
	 */
	public static boolean isInsidePoly( int X[], int Y[], Point P)
	{
		int	n = X.length;
		java.awt.Polygon Poly = new java.awt.Polygon( X, Y, n);
		return Poly.contains( P.x, P.y);
	}
	
	/**
	 * Checks if is inside poly.
	 * 
	 * @param PM the pM
	 * @param P the p
	 * 
	 * @return true, if is inside poly
	 */
	public static boolean isInsidePoly( Point PM[], Point P)
	{
		int	 i, n = PM.length;
		int X[] = new int[ n];
		int Y[] = new int[ n];
		for( i = n; --i >= 0;){
			X[i] = PM[i].x;	
			Y[i] = PM[i].y;	
		}
		java.awt.Polygon Poly = new java.awt.Polygon( X, Y, n);
		return Poly.contains( P.x, P.y);
	}
	
	/**
	 * D line length.
	 * 
	 * @param dX0 the d x0
	 * @param dY0 the d y0
	 * @param dX1 the d x1
	 * @param dY1 the d y1
	 * 
	 * @return the double
	 */
	public static double dLineLength( double dX0, double dY0, double dX1, double dY1)
	{
		dX0 -= dX1;
		dY0 -= dY1;
		return	Math.sqrt( dX0*dX0 + dY0*dY0);
	}
	
	/**
	 * D line length.
	 * 
	 * @param P0 the p0
	 * @param P1 the p1
	 * 
	 * @return the double
	 */
	public static double dLineLength( Point2D.Double P0, Point2D.Double P1)
	{
	    if( P0 == null || P1 == null ) return -1;
		double dX = P0.x - P1.x;
		double dY = P0.y - P1.y;
		return	Math.sqrt( dX*dX + dY*dY);
	}
	
	/**
	 * D line length.
	 * 
	 * @param P0 the p0
	 * @param P1 the p1
	 * 
	 * @return the double
	 */
	public static double dLineLength( Point2D P0, Point2D P1)
	{
	    if( P0 == null || P1 == null ) return -1;
		double dX = P0.getX() - P1.getX();
		double dY = P0.getY() - P1.getY();
		return	Math.sqrt( dX*dX + dY*dY);
	}
	
	/**
	 * D line length.
	 * 
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dLineLength( double[] Points)
	{ 
		Point2D.Double Ps = null, Pe = null;
		double dLength = 0.0;
		int n = Points.length;

		for( int i = 0; i < n*2;  ){ 
			Pe = new Point2D.Double( Points[ i++], Points[ i++]); 
			if( Ps != null ){
				dLength += GlobalFunc.dLineLength( Ps,Pe);
			}
			Ps = Pe;	
		}
		return dLength;
	}
	
	/**
	 * D line length.
	 * 
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dLineLength( Point2D[] Points)
	{ 
		Point2D Ps = null, Pe = null;
		double dLength = 0.0;
		int n = Points.length;

		for( int i = n;  --i >= 0;  ){ 
			Pe = Points[ i]; 
			if( Ps != null ) dLength += GlobalFunc.dLineLength( Ps,Pe);
			Ps = Pe;	
		}
		return dLength;
	}
	
	/**
	 * D line length.
	 * 
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dLineLength( Point2D.Double[] Points)
	{ 
		Point2D.Double Ps = null, Pe = null;
		double dLength = 0.0;
		int n = Points.length;

		for( int i = n;  --i >= 0;  ){ 
			Pe = Points[ i]; 
			if( Ps != null ) dLength += GlobalFunc.dLineLength( Ps,Pe);
			Ps = Pe;	
		}
		return dLength;
	}
	
	/**
	 * I line length.
	 * 
	 * @param P0 the p0
	 * @param P1 the p1
	 * 
	 * @return the int
	 */
	public static int iLineLength( Point P0, Point P1)
	{
		int iX = P0.x - P1.x;
		int iY = P0.y - P1.y;
		if( iX == 0 ) return iY;
		if( iY == 0 ) return iX;
		return	( int)Math.sqrt( ( double)iX*iX + ( double)iY*iY);
	}
	
	/**
	 * Length to line.
	 * 
	 * @param Pr the pr
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the int
	 */
	public static int lengthToLine( Point Pr, Point Ps, Point Pe)
	{
		long lx = Pe.x - Ps.x, ly = Pe.y - Ps.y;
		double rx = Pr.x - Ps.x, ry = Pr.y - Ps.y;	
		double sx = Pr.x - Pe.x, sy = Pr.y - Pe.y;	
		int iLength;           

		if( lx == 0 ) iLength = (ry*sy<=0)? RoundToInt( Math.abs( rx)): -1;
		else if( ly == 0 ) iLength = (rx*sx<=0)? RoundToInt( Math.abs( ry)): -1;
		else{
			sx = rx*lx + ry*ly;
			sy = rx*ly - ry*lx;
			rx = Math.sqrt( lx*lx + ly*ly);
			sx /= rx;	sy /= rx;
			iLength = ( sx >= 0 && sx <= rx )? RoundToInt( Math.abs( sy)): -1;
		}
		return iLength;
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
		double dLength;           

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
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the double
	 */
	public static double dLengthToLine( Point2D.Double Pr, Point2D.Double Ps, Point2D.Double Pe)
	{
		double lx = Pe.x - Ps.x, ly = Pe.y - Ps.y;
		double rx = Pr.x - Ps.x, ry = Pr.y - Ps.y;	
		double sx = Pr.x - Pe.x, sy = Pr.y - Pe.y;	
		double dLength;           

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
	 * D length to line2.
	 * 
	 * @param Pr the pr
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the double
	 */
	public static double dLengthToLine2( Point2D Pr, Point2D Ps, Point2D Pe)
	{
		double dLength = dLengthToLine( Pr, Ps, Pe);
		if( dLength < 0 ){
			double dLs = dLineLength( Pr, Ps);
			double dLe = dLineLength( Pr, Pe);
			dLength = (dLs<dLe)? dLs: dLe;
		}
		return dLength;
	}
	
	/**
	 * D length to line2.
	 * 
	 * @param Pr the pr
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the double
	 */
	public static double dLengthToLine2( Point2D.Double Pr, Point2D.Double Ps, Point2D.Double Pe)
	{
		double dLength = dLengthToLine( Pr, Ps, Pe);
		if( dLength < 0 ){
			double dLs = dLineLength( Pr, Ps);
			double dLe = dLineLength( Pr, Pe);
			dLength = (dLs<dLe)? dLs: dLe;
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
				Point2D Pe = Points[ i]; // new Point2D.Double( Points[ i++], Points[ i++]); 
				double dLen = Pe.distance( Pr);
				if( dLen >= 0.0 && ( !bFound || dLen < dMinLen) ){ 
					dMinLen = dLen;
					bFound = true;
				} 
				if( Ps != null ){
					dLen = GlobalFunc.dLengthToLine( Pr, Ps, Pe);
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
	 * @param PS the pS
	 * @param PE the pE
	 * 
	 * @return the double
	 */
	public static double dLengthToLine( Point2D Pr, Point2D[] Points, Point2D PS, Point2D PE)
	{ 
		Point2D Ps = PS;
		int n = (Points==null)? 0: Points.length;
		boolean bFound = false;
		double dMinLen = -1, dLen = 0;

		for( int i = 0; i < n; ++i){ 
			Point2D Pe = Points[ i];
			dLen = Pe.distance( Pr);
			if( dLen >= 0.0 && ( !bFound || dLen < dMinLen) ){ 
				dMinLen = dLen;
				bFound = true;
			} 
			dLen = GlobalFunc.dLengthToLine( Pr, Ps, Pe);
			if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
				dMinLen = dLen;
				bFound = true;
			} 
			Ps = Pe;	
		}
		dLen = GlobalFunc.dLengthToLine( Pr, Ps, PE);
		if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
			dMinLen = dLen;
			bFound = true;
		} 
		return dMinLen;
	}
/*	public static double dLengthToLine( Point2D Pr, Point2D[] Points, Point2D PS, Point2D PE)
	{ 
		Point2D.Double Ps = PS;
		int n = (Points==null)? 0: Points.length;
		boolean bFound = false;
		double dMinLen = -1, dLen = 0;

		for( int i = 0; i < n; ++i){ 
			Point2D.Double Pe = Points[ i];
			dLen = Pe.distance( Pr);
			if( dLen >= 0.0 && ( !bFound || dLen < dMinLen) ){ 
				dMinLen = dLen;
				bFound = true;
			} 
			dLen = GlobalFunc.dLengthToLine( Pr, Ps, Pe);
			if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
				dMinLen = dLen;
				bFound = true;
			} 
			Ps = Pe;	
		}
		dLen = GlobalFunc.dLengthToLine( Pr, Ps, PE);
		if( dLen >= 0.0  && ( !bFound || dLen < dMinLen) ){
			dMinLen = dLen;
			bFound = true;
		} 
		return dMinLen;
	}*/
//	public static double dLengthToLine( float[] Points, float dX, float dY)
//	{ 
//		return dLengthToLine( new Point2D.Double( dX, dY), Points);
//	}
	/**
 * D length to proj.
 * 
 * @param Pr the pr
 * @param Ps the ps
 * @param Pe the pe
 * 
 * @return the double
 */
public static double dLengthToProj( Point2D.Double Pr, Point2D.Double Ps, Point2D.Double Pe)
	{
		double lx = Pe.x - Ps.x, ly = Pe.y - Ps.y;
		double rx = Pr.x - Ps.x, ry = Pr.y - Ps.y;	
		double sx = Pr.x - Pe.x, sy = Pr.y - Pe.y;	
		double dLength;           

		if( lx == 0 ) dLength = (ry*sy<=0)? Math.abs( ry): -1;
		else if( ly == 0 ) dLength = (rx*sx<=0)? Math.abs( rx): -1;
		else{
			sx = rx*lx + ry*ly;
			sy = rx*ly - ry*lx;
			rx = Math.sqrt( lx*lx + ly*ly);
			sx /= rx;	sy /= rx;
			dLength = ( sx >= 0 && sx <= rx )? sx: -1;
		}
		return dLength;
	}
	
	/**
	 * Gets the line proj point.
	 * 
	 * @param Pr the pr
	 * @param Ps the ps
	 * @param Pe the pe
	 * 
	 * @return the line proj point
	 */
	public static Point2D.Double getLineProjPoint( Point2D Pr, Point2D Ps, Point2D Pe)
	{
		double lx = Pe.getX() - Ps.getX(), ly = Pe.getY() - Ps.getY();
		double rx = Pr.getX() - Ps.getX(), ry = Pr.getY() - Ps.getY();	
		double sx = Pr.getX() - Pe.getX(), sy = Pr.getY() - Pe.getY();	
		double dLength;           

		if( lx == 0 ) dLength = (ry*sy<=0)? Math.abs( ry): -1;
		else if( ly == 0 ) dLength = (rx*sx<=0)? Math.abs( rx): -1;
		else{
			sx = rx*lx + ry*ly;
			sy = rx*ly - ry*lx;
			rx = Math.sqrt( lx*lx + ly*ly);
			sx /= rx;	sy /= rx;
			dLength = ( sx >= 0 && sx <= rx )? sx: -1;
		}
		return (dLength >= 0)? new Point2D.Double( Ps.getX() + lx / rx * dLength, Ps.getY() + ly / rx * dLength): null;
	}
	
	/**
	 * D length to zigzag proj.
	 * 
	 * @param Points the points
	 * @param Pr the pr
	 * 
	 * @return the double
	 */
	public static double dLengthToZigzagProj( double[] Points, Point2D.Double Pr)
	{ 
		double dMinLen = -1, dLen;
		Point2D.Double Ps = null, Pe = null;
		int n = Points.length;
		int iMin = -1;
//		double dLength = 0.0;
		double dProjLength = -1;

		for( int i = 0; i < n;  ){ 
			Pe = new Point2D.Double( Points[ i++], Points[ i++]); 
			dLen = Pe.distance( Pr);
			if( dLen >= 0.0 && ( iMin < 0 || dLen < dMinLen) ){ 
				dMinLen = dLen;
				iMin = i-2;
			}
			Ps = Pe;	
		}
		if( iMin >= 0 ){
			double dP1 = -1; 
			if( iMin > 0 ){
				Ps = new Point2D.Double( Points[ iMin-2], Points[ iMin-1]); 
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
			if( iMin > 0 ) dProjLength += dZigzagLength( Points, iMin/2+1);
		}			
		return dProjLength;
	}
	
	/**
	 * D length to zigzag proj.
	 * 
	 * @param Points the points
	 * @param dX the d x
	 * @param dY the d y
	 * 
	 * @return the double
	 */
	public static double dLengthToZigzagProj( double[] Points, double dX, double dY)
	{ 
		Point2D.Double Pr = new Point2D.Double( dX, dY); 
		return dLengthToZigzagProj( Points, Pr);
	}
	
	/**
	 * D zigzag length.
	 * 
	 * @param Points the points
	 * 
	 * @return the double
	 */
	public static double dZigzagLength( double[] Points)
	{ 
		return dZigzagLength( Points, Points.length/2);
/*		Point2D.Double Ps = null, Pe = null;
		int n = Points.length;
		double dLength = 0.0;

		for( int i = 0; i < n;  ){ 
			Pe = new Point2D.Double( Points[ i++], Points[ i++]); 
			if( Ps != null ){
				dLength += GlobalFunc.dLineLength( Ps,Pe);
			}
			Ps = Pe;	
		}
		return dLength; */
	}
	
	/**
	 * D zigzag length.
	 * 
	 * @param Points the points
	 * @param n the n
	 * 
	 * @return the double
	 */
	public static double dZigzagLength( double[] Points, int n)
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
/*	public static boolean execSQL( URL URLName, String sSQL)
//	public static boolean execSQL( String sSQL)
	{
		String sOut = "execSQL.asp?SQL=" + sSQL;
// Parent.Parent.messageBox( sOut);
		try{
			InputStream hFile = ( new URL( URLName, sOut)).openStream();
			int iRet = hFile.read() - '0';
			hFile.close();
			if( iRet != 0 ) return true;
//			return iRet;
		}
		catch( IOException e)
		{
		}
		return false;
//		return 0;
	}
	public static boolean deleteDBElement( URL URLName, String sFName, String sWhere)
	{
		String sOut = "DELETE FROM " + sFName + " WHERE " + sWhere;
		if( GlobalFunc.execSQL( URLName, sOut) ){
			return true;
		}else{
//			Parent.Parent.messageBox( sOut);
			return false;
		}
	}
	public static int insertDBElement( URL URLName, String sFName, String sKey,
		String sAttr, String sValues)
	{
		int	iRet = 0;
		
		String sOut = "insertSQL.asp?TABLE=" + sFName + 
			"&ATTR=" + sAttr + "&VALUES=" + sValues;
		if( sKey != null ) sOut += "&KEY=" + sKey;
		try{
			BufferedReader hFile = new BufferedReader( new InputStreamReader( 
				( new URL( URLName, sOut)).openStream()));
			iRet = new Integer( hFile.readLine()).intValue();
			hFile.close();
		}
		catch( IOException e)
		{
		}
		return iRet;
	}
	public static Double StringToDouble( String sValue)
	{
		int i, n;
		boolean bPoint = false, bBreak = false;
		
		if( sValue != null && (n = sValue.length()) > 0 ){
			for( i = 0; i < n; ++i){
				switch( sValue.charAt( i) ){
				case ',' :
					sValue = sValue.replace( ',', '.');
				case '.' :
					if( bPoint ) bBreak = true;
					else bPoint = true;
					break;
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					break;
				default :
					bBreak = true;
				}
				if( bBreak ) break;
			}
			if( i < n ) sValue = sValue.substring( 0, i);
			if( i > 0 ) return new Double( sValue);
		}
		return null;
	}
	public static Integer StringToInteger( String sValue)
	{
		int i, n;
		
		if( sValue != null && (n = sValue.length()) > 0 ){
			for( i = 0; i < n; ++i){
				char cSymb = sValue.charAt( i);
				if( cSymb < '0' || cSymb > '9' ) break;
			}
			if( i < n ) sValue = sValue.substring( 0, i);
			if( i > 0 ) return new Integer( sValue);
		}
		return null;
	} */
	/**
 * Gets the time string.
 * 
 * @param iTime the i time
 * 
 * @return the time string
 */
public static String getTimeString( int iTime)
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
		}else{		
			if( iM > 0 )  sTime.append( iM);		
			else sTime.append( "0");
		}
		sTime.append( ".");
		if( iS >= 10 ) sTime.append( iS);
		else if( iS > 0 )  sTime.append( "0" + iS);		
		else sTime.append( "00");
		return  sTime.toString();
	}
	
	/**
	 * Gets the time string m.
	 * 
	 * @param iTime the i time
	 * 
	 * @return the time string m
	 */
	public static String getTimeStringM( int iTime)
	{
		double dTime = iTime/60;
		dTime += (iTime - iTime/60*60)*0.01;
		return GlobalFunc.DoubleToString( dTime, 2);
	}
	
	/**
	 * Gets the time t string.
	 * 
	 * @param iTime the i time
	 * 
	 * @return the time t string
	 */
	public static String getTimeTString( int iTime)
	{
		return getTimeString( ( iTime + 5) / 10);
	}
	
	/**
	 * Gets the current date.
	 * 
	 * @return the current date
	 */
	public static String getCurrentDate()
	{
		java.util.Date D = new java.util.Date();
		return D.toString();
	}
	
	/**
	 * Gets the current time.
	 * 
	 * @return the current time
	 */
	public static String getCurrentTime()
	{
		long lTime = ( new java.util.Date()).getTime();
		lTime %= 24 * 3600000;
		return getTimeString( (int)(lTime + 500) / 1000);
	}
	
	/**
	 * Gets the current date time.
	 * 
	 * @return the current date time
	 */
	public static java.sql.Date getCurrentDateTime()
	{
		return new Date( ( new java.util.Date()).getTime());
	}

	/**
	 * Gets the period.
	 * 
	 * @param lSTime the l s time
	 * 
	 * @return the period
	 */
	public static String getPeriod( long lSTime)
	{
		long lETime = ( new java.util.Date()).getTime() - lSTime + 50;
		int iTimeS = (int)lETime / 1000; 
		String sTime = getTimeString( iTimeS);
		int iTimeM = (int)(( lETime - iTimeS * 1000) / 100);
		return sTime + "," + iTimeM;
//		int iTimeS = (int)(lETime + 500) / 1000; 
//		return getTimeString( iTimeS);
	}
	public static String getPeriodMS( long lSTime)
	{
		long lETime = ( new java.util.Date()).getTime() - lSTime;
		return lETime + "ms";
	}
	
	/**
	 * Gets the length string.
	 * 
	 * @param iLength the i length
	 * 
	 * @return the length string
	 */
	public static String getLengthString( int iLength)
	{
		iLength = (iLength + 50) / 100; 
		
		int iK = iLength / 10;
		int iM = iLength % 10;
		
		return ((iK>0)? iK: 0) + "." + ((iM>0)? iM: 0) + " km";  
	}
	
	/**
	 * Gets the speed string.
	 * 
	 * @param iLength the i length
	 * @param iTime the i time
	 * 
	 * @return the speed string
	 */
	public static String getSpeedString( int iLength, int iTime)
	{
		return Math.round( ( double)iLength / iTime * 3.6) + " km/h";
	}
	
	/**
	 * Gets the speed t string.
	 * 
	 * @param iLength the i length
	 * @param iTime the i time
	 * 
	 * @return the speed t string
	 */
	public static String getSpeedTString( int iLength, int iTime)
	{
		return Math.round( ( double)iLength / iTime * 36) + " km/h";
	}
	
	/**
	 * Gets the speed string.
	 * 
	 * @param iSpeed the i speed
	 * 
	 * @return the speed string
	 */
	public static String getSpeedString( int iSpeed)
	{
		return iSpeed + " km/h";
	}
	
	/**
	 * String to string array.
	 * 
	 * @param sString the s string
	 * 
	 * @return the string[]
	 */
	public static String[] StringToStringArray( String sString)
	{
		int n = 0;
		for( int i0 = 0;;){
			int i = sString.indexOf( ',', i0);
			++n;
			if( i >= 0 ) i0 = ++i;
			else break;
		}
		String[] A = new String[ n];	

		int i = sString.indexOf( ',');
		for( int i0 = 0; --n >= 0;){
			if( i > 0 ){
				A[ n] = new String( sString.substring( i0, i));	
				i = sString.indexOf( ',', i0);
			}else{
				A[ n] = new String( sString.substring( i0));	
				break;	
			}
		}
		return A;
	}
	
	/**
	 * Checks if is in string.
	 * 
	 * @param s1 the s1
	 * @param s2 the s2
	 * 
	 * @return true, if is in string
	 */
	public static boolean isInString( String s1, String s2)
	{
		int i1 = s1.indexOf( s2);
		int n = s2.length();
		
		return i1 >= 0 && (i1 == 0 || s1.charAt( i1-1) == ',') &&
			(i1 + n == s1.length() || s1.charAt( i1+n) == ',');	
	}
	
	/**
	 * Strings intersection.
	 * 
	 * @param s1 the s1
	 * @param s2 the s2
	 * 
	 * @return the string
	 */
	public static String StringsIntersection( String s1, String s2)
	{
		StringBuffer s = new StringBuffer();
		for( int i0 = 0;;){
			int i1 = s1.indexOf( ',', i0), i2, n;

			if( i1 > 0 ){
				i2 = s2.indexOf( s1.substring( i0, i1));
				n = i1 - i0;	
			}else{
				i2 = s2.indexOf( s1.substring( i0));
				n = s1.length() - i0;	
			}
			if( i2 >= 0 && (i2 == 0 || s2.charAt( i2-1) == ',') &&
				(i2 + n == s2.length() || s2.charAt( i2+n) == ',') ){ 
				if( s.length() > 0 ) s.append( ",");
				s.append( s1.substring( i0, i0+n));
			}
			if( i1 < 0 ) break;
			i0 = ++i1;
		}
		return s.toString();
	}
	
	/**
	 * String to int.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the int
	 */
	public static int StringToInt( String sValue)
	{
		int iSign = 1;
		int iRet = 0;
		if( sValue != null )
		for( int i = 0; i < sValue.length(); ++i){
			char c = sValue.charAt( i);
			int iC = Character.digit( c, 10);
			if( iC < 0 ){
				 if( iRet == 0 ){
					if( c == '-' ) iSign = -1;
					else if( c == '+' ) iSign = 1;
					else if( c != ' ' ) break;
				 }else break;
			}else iRet = iRet * 10 + iC;
		}
		return iRet * iSign;
	}
/*ublic static java.sql.Date parseDate( String sDate) 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss.SSSzzz");
		try{
			java.util.Date date = DF.parse( sDate);  
			return new java.sql.Date( date.getTime()); 
		} catch (ParseException e) {
		}
		return null;
	} */
	/**
 * Parses the date.
 * 
 * @param sDate the s date
 * @param bStart the b start
 * 
 * @return the java.sql. date
 */
public static java.sql.Date parseDate( String sDate, boolean bStart) 
	{
		
		if( sDate == null ) return null;
		Calendar Date = GregorianCalendar.getInstance();

// Date fom dd.mm.yyyy hh:mm
//		java.sql.Date D = new java.sql.Date( ( new java.util.Date()).getTime());
		int iD=0, iM=0, iY=0, iH = 0, iMin = 0;
	try{		
		int i = 0;
		char cSymb;

		while( ( cSymb = sDate.charAt( i++)) == ' ' );
		if( cSymb >= '0' && cSymb <= '9' ){
			iD = cSymb - '0';
			cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iD = iD*10 + cSymb - '0';
				cSymb = sDate.charAt( i++);
			}
		}
		if( cSymb == '.' ){
			cSymb = sDate.charAt( i++);
			if( cSymb >= '0' && cSymb <= '9' ){
				iM = cSymb - '0';
				cSymb = sDate.charAt( i++);
				if( cSymb >= '0' && cSymb <= '9' ){
					iM = iM*10 + cSymb - '0';
					cSymb = sDate.charAt( i++);
				}
			}
			if( cSymb == '.' ){
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
				if( cSymb >= '0' && cSymb <= '9' ) iMin = iMin*10 + cSymb - '0';
			}
		}
	}catch( IndexOutOfBoundsException e ){
		if( iH == 0 && !bStart ) iH = 24;
	}
		if( iD == 0 ){
			if( iM == 0 ) iD = Date.get( GregorianCalendar.DAY_OF_MONTH);
			else{  // antud on kuu, päev andmata
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

		Date = new GregorianCalendar( iY, iM, iD, iH, iMin);
//		if( Date == null ) return null; 
		java.util.Date DateU = Date.getTime(); 		
		return new java.sql.Date( DateU.getTime()); 
	}	
	
	/**
	 * Parses the date to string.
	 * 
	 * @param sDate the s date
	 * @param bStart the b start
	 * 
	 * @return the string
	 */
	public static String parseDateToString( String sDate, boolean bStart) 
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return DFOut.format( GlobalFunc.parseDate( sDate, bStart));
	}
	
	/**
	 * Date to string.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString(  java.sql.Date D) 
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return new String( DFOut.format( D));
	}
	
	/**
	 * Date to string00.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString00(  java.util.Date D) 
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy 00:00"); 
		return new String( DFOut.format( D));
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
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy 24:00"); 
		return new String( DFOut.format( D));
	}
	
	/**
	 * Date to string.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString(  java.util.Date D) 
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy HH:mm"); 
		return new String( DFOut.format( D));
	}
	
	/**
	 * Date to string2.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString2(  java.sql.Date D) 
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( D));
	}
	
	/**
	 * Date to string2.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToString2(  java.util.Date D) 
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "dd.MM.yyyy"); 
		return new String( DFOut.format( D));
	}
	
	/**
	 * Date to string.
	 * 
	 * @param iD the i d
	 * @param iM the i m
	 * @param iY the i y
	 * 
	 * @return the string
	 */
	public static String DateToString( int iD, int iM, int iY) 
	{
		DecimalFormat F = new DecimalFormat( "00");
		return F.format( iD) + "." + F.format( iM) + "." + F.format( iY); 
	}
	
	/**
	 * Parses the date to sql.
	 * 
	 * @param sDate the s date
	 * @param bStart the b start
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public static String parseDateToSQL( String sDate, boolean bStart) throws MException  
	{
		if( sDate == null || sDate.length() <= 0 ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd HH:mm"); 
		java.sql.Date D = GlobalFunc.parseDate( sDate, bStart);
//System.out.println( sDate);
//System.out.println( D.toString() + " " + D.getTime());
//System.out.println( DFOut.format( D));
		return DFOut.format( D);
	}
	
	/**
	 * Date to sql.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 */
	public static String DateToSQL( java.sql.Date D)
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
		return DFOut.format( D);
	}
	
	/**
	 * Date to sq l2.
	 * 
	 * @param D the d
	 * 
	 * @return the string
	 * 
	 * @throws MException the m exception
	 */
	public static String DateToSQL2( java.sql.Date D) throws MException  
	{
		if( D == null ) return null;
		SimpleDateFormat DFOut = new SimpleDateFormat( "yyyy-MM-dd"); 
		return DFOut.format( D);
	}
	
	/**
	 * Gets the time hm.
	 * 
	 * @param lTime the l time
	 * 
	 * @return the time hm
	 */
	public static String getTimeHM( long lTime)
	{
		lTime += 30; // 000;
//		lTime /= 1000;
		long lH = lTime / 3600;
		long lM = (lTime % 3600) / 60;
		if( lM >= 10 )
		    return lH + " h " + lM + " min";
		else
		    return lH + " h 0" + lM + " min";
	}
	
	/**
	 * Gets the time h m2.
	 * 
	 * @param lTime the l time
	 * 
	 * @return the time h m2
	 */
	public static String getTimeHM2( long lTime)
	{
		lTime += 30; // 000;
//		lTime /= 1000;
		long lH = lTime / 3600;
		long lM = (lTime % 3600) / 60;
		if( lM >= 10 )
		    return lH + "." + lM;
		else
		    return lH + ".0" + lM;
	}
	
	/**
	 * Round hm.
	 * 
	 * @param lTime the l time
	 * 
	 * @return the long
	 */
	public static long roundHM( long lTime)
	{
		lTime += 30; // 000;
		long lH = lTime / 3600;
		long lM = (lTime % 3600) / 60;
		return (lH*60 + lM) * 60;
	}
	
	/**
	 * Round hm.
	 * 
	 * @param lTime the l time
	 * 
	 * @return the int
	 */
	public static int roundHM( int lTime)
	{
		lTime += 30; // 000;
		int lH = lTime / 3600;
		int lM = (lTime % 3600) / 60;
		return (lH*60 + lM) * 60;
	}
	
	/**
	 * Round hm.
	 * 
	 * @param dTime the d time
	 * 
	 * @return the long
	 */
	public static long roundHM( double dTime)
	{
	    return roundHM( Math.round( dTime));
	}
	
	/**
	 * Gets the length km.
	 * 
	 * @param lLength the l length
	 * 
	 * @return the length km
	 */
	public static String getLengthKM( long lLength)
	{
		lLength += 50;
		lLength /= 100;
		return lLength/10 + "." + lLength%10 + " km";
	}
	public static String getLengthKM( double dLength)
	{
		return getLengthKM( Math.round( dLength));
	}
	
	/**
	 * Gets the length k m2.
	 * 
	 * @param lLength the l length
	 * 
	 * @return the length k m2
	 */
	public static String getLengthKM2( long lLength)
	{
		lLength += 50;
		lLength /= 100;
		return lLength/10 + "." + lLength%10 + " km";
	}
	
	/**
	 * Gets the length k m3.
	 * 
	 * @param iLength the i length
	 * 
	 * @return the length k m3
	 */
	public static String getLengthKM3( int iLength)
	{
		if( iLength >= 900 ) return getLengthKM( iLength);
		return ( iLength >= 5 )? (iLength + 5)/10 + "0 m": "0 m";
	}
	
	/**
	 * Byte to hexa.
	 * 
	 * @param b the b
	 * 
	 * @return the string
	 */
	public static  String ByteToHexa( byte b)
	{
		int i = b;
		if( i < 0 ) i += 256;
		return ByteToHexa( i);
/*		int i1 = i / 16;
		int i2 = i % 16;
		StringBuffer sBuf = new StringBuffer();
		sBuf.append( ( char)((i1 <= 9)? '0'+i1 : 'A'+i1-10));
		sBuf.append( ( char)((i2 <= 9)? '0'+i2 : 'A'+i2-10));
		return sBuf.toString();*/
	}
	
	/**
	 * Byte to hexa.
	 * 
	 * @param i the i
	 * 
	 * @return the string
	 */
	public static  String ByteToHexa( int i)
	{
		int i1 = i / 16;
		int i2 = i % 16;
		StringBuffer sBuf = new StringBuffer();
		sBuf.append( ( char)((i1 <= 9)? '0'+i1 : 'A'+i1-10));
		sBuf.append( ( char)((i2 <= 9)? '0'+i2 : 'A'+i2-10));
		return sBuf.toString();
	}
	
	/**
	 * Memory dump.
	 * 
	 * @param Buf the buf
	 * @param iCount the i count
	 */
	public static void MemoryDump( ByteBuffer Buf, int iCount)
	{
		for( int j0=0; j0<iCount;){
			StringBuffer sBuf = new StringBuffer(); 
			for( int j=30; --j>=0 && j0<iCount;){ 
				sBuf.append( GlobalFunc.ByteToHexa( Buf.get( j0++) ));		 			
				if( (j&1) == 0 ) sBuf.append( ' ');
			}
			Log.info( sBuf.toString());
		}
	}
	
	/**
	 * Gets the area string.
	 * 
	 * @param dArea the d area
	 * 
	 * @return the area string
	 */
	public static String getAreaString( double dArea)
	{
		long lArea = Math.round( dArea);
		if( lArea >= 10000 ){
			lArea = Math.round( dArea/10000);
			if( lArea >= 10000 ){
			    lArea = Math.round( dArea/1000000);
				return lArea + "km²"; 
			}
			return lArea + "ha";
		}		
		return lArea + "m²";
	}
	
	/**
	 * Ge polygon.
	 * 
	 * @param Points the points
	 * 
	 * @return the java.awt. polygon
	 */
	public static java.awt.Polygon gePolygon(  Point2D.Double [] Points) {
		
		if( Points == null ) return null;
		int	 i, n = Points.length;
		int X[] = new int[ n];
		int Y[] = new int[ n];
		for( i = n; --i >= 0;){
			X[i] = ( int)Math.round( Points[i].x);	
			Y[i] = ( int)Math.round( Points[i].y);	
		}
		return new java.awt.Polygon( X, Y, n);
	}
	
	/**
	 * Gets the int.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the int
	 */
	public static int getInt( String sValue)
	{
		int iRet = 0;
		try {
			if( sValue != null ) iRet = ( int)Math.round( Double.parseDouble( sValue));
		} catch ( Exception e) {
		}
        return iRet;
	}
	
	/**
	 * Gets the int.
	 * 
	 * @param Value the value
	 * @param i the i
	 * 
	 * @return the int
	 */
	public static int getInt( String [] Value, int i)
	{
		return (Value!=null && i>=0 && i<Value.length )? getInt( Value[ i]): 0;
	}
	
	/**
	 * Gets the double.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the double
	 */
	public static double getDouble( String sValue)
	{
		double dRet = 0;
		try {
			if( sValue != null ) dRet = Double.parseDouble( sValue);
		} catch ( Exception e) {
		}
        return dRet;
	}
	
	/**
	 * Gets the string.
	 * 
	 * @param Value the value
	 * @param i the i
	 * 
	 * @return the string
	 */
	public static String getString( String [] Value, int i)
	{
		return (Value!=null && i>=0 && i<Value.length )? Value[ i]: null;
	}
	
	/**
	 * Gets the double.
	 * 
	 * @param Value the value
	 * @param i the i
	 * 
	 * @return the double
	 */
	public static double getDouble( String [] Value, int i)
	{
		return (Value!=null && i>=0 && i<Value.length )? getDouble( Value[ i]): 0;
	}
	
	/**
	 * Gets the time m.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the time m
	 */
	public static int getTimeM( String sValue)
	{
		double dTime = getDouble( sValue.replace( ':', '.'));
		int iH = ( int)Math.floor( dTime);
		int iM = ( int)Math.round( (dTime - iH)*100);
		return iH*60 + iM;
	}
	
	/**
	 * Gets the time s.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the time s
	 */
	public static int getTimeS( String sValue)
	{
		double dTime = getDouble( sValue);
		int iH = ( int)Math.floor( dTime);
		int iM = ( int)Math.round( (dTime - iH)*100);
		return (iH*60 + iM)*60;
	}
	
	/**
	 * Gets the time m.
	 * 
	 * @param Value the value
	 * @param i the i
	 * 
	 * @return the time m
	 */
	public static int getTimeM( String [] Value, int i)
	{
		return (Value!=null && i>=0 && i<Value.length )? getTimeM( Value[ i]): 0;
	}
	
	/**
	 * Gets the length km.
	 * 
	 * @param sValue the s value
	 * 
	 * @return the length km
	 */
	public static int getLengthKM( String sValue)
	{
		double dLength = getDouble( sValue);
		return ( int)Math.round( dLength * 1000);
	}
	
	/**
	 * Gets the length km.
	 * 
	 * @param Value the value
	 * @param i the i
	 * 
	 * @return the length km
	 */
	public static int getLengthKM( String [] Value, int i)
	{
		return (Value!=null && i>=0 && i<Value.length )? getLengthKM( Value[ i]): 0;
	}
}
