package fr.cned.emdsgil.suividevosfrais;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

class FraisHfAdapter extends BaseAdapter {

	private final ArrayList<FraisHf> lesFrais ; // liste des frais du mois
	private final LayoutInflater inflater ;
	private final int key;
    /**
	 * Constructeur de l'adapter pour valoriser les propriétés
     * @param context Accès au contexte de l'application
     * @param lesFrais Liste des frais hors forfait
     */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais, int key) {
		inflater = LayoutInflater.from(context) ;
		// initialisation de la liste des FraisHf
		this.lesFrais = lesFrais ;
		// initialisation de la clé permettant de selectionner le mois
		this.key = key;
    }

	/**
	 * retourne le nombre d'éléments de la listview
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * structure contenant les éléments d'une ligne
	 */
	private class ViewHolder {
		TextView txtListJour ;
		TextView txtListMontant ;
		TextView txtListMotif ;
		ImageButton ib;
	}
	
	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(final int index, View convertView, final ViewGroup parent) {
		final ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder() ;
			convertView = inflater.inflate(R.layout.layout_liste, parent, false) ;
			holder.txtListJour = convertView.findViewById(R.id.txtListJour);
			holder.txtListMontant = convertView.findViewById(R.id.txtListMontant);
			holder.txtListMotif = convertView.findViewById(R.id.txtListMotif);


			// Récuperation du bouton et ajout du listener onClick pour la suppression du HF
			holder.ib = convertView.findViewById(R.id.cmdSuppHf);
			holder.ib.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {


					//Supprime de la liste le HF
					Global.listFraisMois.get(key).getLesFraisHf().remove(index);

					//affichage de la liste à nouveau sans le HF supprimé
					//System.out.println("nouveau : " + Global.listFraisMois.get(202003).getLesFraisHf());

					//Serialise sans l'objet supprimé
					Serializer.serialize(Global.listFraisMois, MainActivity.instance);

					//Met à jour la vue afin de retirer le HF supprimé
					FraisHfAdapter.super.notifyDataSetChanged();
				}
			});
			convertView.setTag(holder) ;
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.txtListJour.setText(String.format(Locale.FRANCE, "%d", lesFrais.get(index).getJour()));
		holder.txtListMontant.setText(String.format(Locale.FRANCE, "%.2f", lesFrais.get(index).getMontant())) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;
		return convertView ;
	}

}
