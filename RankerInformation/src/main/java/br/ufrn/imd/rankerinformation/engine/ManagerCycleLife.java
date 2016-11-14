package br.ufrn.imd.rankerinformation.engine;

import java.util.List;
import br.ufrn.imd.rankerinformation.dao.PreferencesDAO;
import br.ufrn.imd.rankerinformation.dao.UserDAO;
import br.ufrn.imd.rankerinformation.engine.filter.Analyzer;
import br.ufrn.imd.rankerinformation.engine.filter.IntersectionModelAssociation;
import br.ufrn.imd.rankerinformation.engine.model.Information;
import br.ufrn.imd.rankerinformation.oauth.RequestAuthorization;
import br.ufrn.imd.rankerinformation.user.model.Preferences;
import br.ufrn.imd.rankerinformation.user.model.SourceData;
import br.ufrn.imd.rankerinformation.user.model.User;
import br.ufrn.imd.rankerinformation.user.search.PreferencesBuilder;
import br.ufrn.imd.rankerinformation.user.search.PreferencesProviderSearch;

public class ManagerCycleLife implements Observer {

	private int iduserCycleLife;
	private Preferences preferences;
	
	public ManagerCycleLife(int iduserCycleLife){
		this.iduserCycleLife = iduserCycleLife;
	}
	
	public void setup(String acess_token, PreferencesProviderSearch preferencesProviderSearch){
		UserDAO userDAO = new UserDAO();
		User user = userDAO.readUser(iduserCycleLife);
		if(user == null || user.getId() == 0){
			try {

				RequestAuthorization authorization = new RequestAuthorization(acess_token);
				PreferencesBuilder prefBuilder = new PreferencesBuilder();
				
				prefBuilder.buiderFormASearch(authorization, preferencesProviderSearch);
				
				System.out.println("[SETUP] Dados buscado na API");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		setup();
	}
	
	public void setup(User user, List<SourceData> listSourceData){
		UserDAO userDAO = new UserDAO();
		User userConsult = userDAO.readUser(user.getId());
		if(userConsult == null || userConsult.getId() == 0){
			try {
				PreferencesBuilder prefBuilder = new PreferencesBuilder();
				prefBuilder.builder(user, listSourceData);
				System.out.println("[SETUP] Dados Setados pelo usuário");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("[SETUP] Usuário já existe na base de dados");
		}
		
		setup();
	}
	
	
	private void setup(){
		//Busca no banco
		PreferencesDAO prefferencesDAO = new PreferencesDAO();
		this.preferences = prefferencesDAO.readPrefferencesByIdUser(iduserCycleLife); 
		System.out.println("----------------------");
		System.out.println("[SETUP] Dados buscado no Banco de dados");
		System.out.println("[SETUP] "+preferences.toString());
		System.out.println("----------------------");
	}

	@Override
	public void update(List<Information> informations) {
		Analyzer analyzer = new Analyzer();
		List<Information> analyzedList = analyzer.analyze(informations, preferences, new IntersectionModelAssociation());
	}

	

}