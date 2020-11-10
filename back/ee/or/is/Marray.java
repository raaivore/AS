/*
 * 
 */
package ee.or.is;

//package moritz;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Class Marray.
 * 
 * @author kx
 * 
 * Mıte oli et vıiks olla klass mis hoiaks loetelu paaridena
 * "nimi-v‰‰rtus"
 * Siin siis nimi on String ja v‰‰rtus objekt.
 * Marray hoiaks selliseid paare j‰rjest ja garanteeriks et j‰rjestus ei muutu.
 * Paare saab j‰rjest lisada 	.put( String key, Object value)
 * Kysida saab 					.getKey ( int index)
 * .getValue( int index)
 * .get( String key)
 * Viimage kysimine otsib etteantud vıtme v‰‰rtuse (nime) j‰rgi v‰lja
 * objekti ja tagastab selle.
 * Lisaks veel rida meetodeid: iteraatori kysimine, yhe paari eemaldamine, jne.
 * Realisatsioon vıiks olla lihtne. Paari eemaldamine on harvaesinev soov
 * ja vabal vıib j‰tta 'augu sisse' - lihtsal v‰lja ei n‰idata et yks-kaks vahelt
 * puudu on.
 * Ilmselt on enamasti juba korra andmetega laetud loetelud suht pysivad
 * ja neid ei muudeta.
 * Otsene vajadus Marray j‰rele on seotud andmebaasi columnite loeteluga.
 * Need loetelud vıivad olla nii yhe tabeli columnid kui ka keerulise p‰ringu
 * tulemusena tagastatava suure hulga v‰ljade loetelu.
 */
public class Marray {

	// realiseerimine vıiks olla lihtne. Hetkel 2 varianti:
	//
	// 1. yks ArrayList mis sisaldab klassi Pair objekte.
	//  Pair sisaldab kahte liiget - String keys, Object value
	//  Edasine k‰sitlus tuleneb kıik sellest ja vist ei vaja rohkem selgitusi.
	//
	// 2. kaks ArrayList-i:
	//		ArrayList keys;
	//		ArrayList values;
	//  Antud ArrayList-d peavad olema alati sama elementide arvuga.
	//  Edsine k‰sitlus vast ei vaja rohkem selgitusi.
	
	// Tundub et kaks ArrayList-i on kiirem lahendus ja v‰hem m‰lu vajav.
	// Lahendus kus on Pair objektid t‰hendab t‰iendavalt veel nende objektide
	// loomist ja hiljem mingil hetkel ka nende m‰lu vabastamist - palju pisikesi
	// objektikesi. GC saab nendega tˆˆd teha.

	/** The keys. */
	private ArrayList<String> keys;
	
	/** The values. */
	private ArrayList<Object> values;
	
	/**
	 * Instantiates a new marray.
	 */
	public Marray() {
		keys = new ArrayList<String>();
		values = new ArrayList<Object>();
	}

	/**
	 * Instantiates a new marray.
	 * 
	 * @param initialsize the initialsize
	 */
	public Marray( int initialsize) {
		keys = new ArrayList<String>( initialsize);
		values = new ArrayList<Object>( initialsize);
	}

	/**
	 * Asendame vıi Lisame uue paari - 'nimi' - 'v‰‰rtus'
	 * Kui antud nimega rida on olemas siis asendame selle v‰‰rtuse.
	 * Kui ei ole siis lisatakse olemasolevate paaride loetelu lıppu.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void put( String key, Object value) {

		if( keys.size() != values.size() ) return;
		
		int len = keys.size();
		for (int i = 0; i < len; i++) {
			String xkey = (String) keys.get( i);
			if ( xkey.equalsIgnoreCase( key) ) {
				values.set( i, value);
				return;
			}
		}
		
		// kui siia oleme jıudnud siis ei leitud, lisame
		add( key, value);
	}

	/**
	 * Lisame uue paari - 'nimi' - 'v‰‰rtus'
	 * Paar lisatakse olemasolevate paaride loetelu lıppu.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void add( String key, Object value) {
		keys.add( key);
		values.add( value);
	}

	/**
	 * Tagastame key (ehk nime) vastavalt etteantud indeksile
	 * (ehk siis soovitud positsiooni nimi).
	 * 
	 * @param index the index
	 * 
	 * @return the key
	 */
	public	String getKey( int index) {
		// Me ei testi siin kas index on 'normaalne' - keys annab teada.
		return (String) keys.get( index);
	}

	/**
	 * Tagastame v‰‰rtuse vastavalt etteantud indeksile
	 * (ehk siis soovitud positsiooni v‰‰rtuse).
	 * 
	 * @param index the index
	 * 
	 * @return the value
	 */
	public	Object getValue( int index) {
		// Me ei testi siin kas index on 'normaalne' - values annab teada.
		return values.get( index);
	}

	/**
	 * Tagastame v‰‰rtuse vastavalt etteantud nimele
	 * (ehk siis soovitud nimele vastava v‰‰rtuse).
	 * 
	 * @param key the key
	 * 
	 * @return the object
	 * 
	 * @throws MException the m exception
	 */
	public	Object get( String key) throws MException {

		if ( keys.size() != values.size() ) {
			// Tısine viga - arvud peavad kokku langema.
			String msg = "Viga, keys arv ja values arv on erinevad";
			Log.info( msg);
			throw new MException( msg);
		}

		int len = keys.size();
		for (int i = 0; i < len; i++) {
			String xkey = (String) keys.get( i);
			if ( xkey.equalsIgnoreCase( key) )
				return values.get( i);
		}
		
		return null;
	}

	/**
	 * Tagastame iteraatori yle vıtmete - ehk nimede.
	 * 
	 * @return the keys iterator
	 */
	public Iterator<String> getKeysIterator() {
		return keys.iterator();
	}

	/**
	 * Tagastame iteraatori yle v‰‰rtuste.
	 * 
	 * @return the values iterator
	 */
	public Iterator<Object> getValuesIterator() {
		return values.iterator();
	}

	/**
	 * Tagastame paaride arvu - ehk siis mitu elementi on antud loetelus.
	 * 
	 * @return the int
	 * 
	 * @throws MException the m exception
	 */
	public int size()
	{
		if ( keys.size() != values.size() ) {
			// Tısine viga - arvud peavad kokku langema.
			String msg = "Viga, keys arv ja values arv on erinevad";
			Log.info( msg);
			return -1;
		}
		return keys.size();
	}

	/**
	 * Eemaldame kogu sisu - teeme nagu oleks uus :).
	 */
	public void clear() {
		keys.clear();
		values.clear();
	}
	public void remove( String key) {
		
		int index = keys.indexOf( key);
		if ( index < 0)
			return;			// pole sellist, pole ka midagi eemaldada.

		keys.remove( index);
		values.remove( index);
	}
	public void remove( int index) {
		
		if ( index < 0)
			return;			// pole sellist, pole ka midagi eemaldada.

		keys.remove( index);
		values.remove( index);
	}

		
}
