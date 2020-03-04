package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe qui permet de sérialiser et désérialiser des objets
 * @author Emds
 *
 */
abstract class Serializer {

	/**
	 * Sérialisation d'un objet
	 * @param object Objet à sérialiser
	 */
	public static void serialize(Object object, Context context) {
		try {
			FileOutputStream file = context.openFileOutput(Global.filename, Context.MODE_PRIVATE) ;
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(file);
				oos.writeObject(object) ;
				oos.flush() ;
				oos.close() ;
			} catch (IOException e) {
				// erreur de sérialisation
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// fichier non trouvé
			e.printStackTrace();
		}
	}
	
	/**
	 * Désérialisation d'un objet
	 * @param context Accès au contexte de l'application
	 * @return Objet déserialisé
	 */
	public static Object deSerialize(Context context) {
		try {
			FileInputStream file = context.openFileInput(Global.filename) ;
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(file);
				try {
					Object object = ois.readObject() ;
					ois.close() ;
					return object ;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// fichier non trouvé
			e.printStackTrace();
		}
		return null ;		
	}
	
}
