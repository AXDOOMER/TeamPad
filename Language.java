
public class Language {

	private String language;

	public Language(String lang) {
		language = lang;
	}

	public void setLanguage(String lang) {
		language = lang;
	}

	public String GetStringForLang(String textId) {
		
		String translation = "?";
		
		if (textId.length() > 5) {
			translation = "?????";
		}
		
		if (language.equals("English")) {
			
			switch (textId)
			{
				case "File": translation = "File"; break;
				case "New": translation = "New"; break;
				case "Open": translation = "Open"; break;
				case "Save": translation = "Save"; break;
				case "Save As": translation = "Save As"; break;
				case "Run": translation = "Run"; break;
				case "Exit": translation = "Exit"; break;
				
				case "Edit": translation = "Edit"; break;
				case "Undo": translation = "Undo"; break;
				case "Redo": translation = "Redo"; break;
				case "Cut": translation = "Cut"; break;
				case "Copy": translation = "Copy"; break;
				case "Paste": translation = "Paste"; break;
				case "Select All": translation = "Select All"; break;
				case "Select Line": translation = "Select Line"; break;
				case "Find and replace": translation = "Find and replace"; break;
				
				case "Option": translation = "Option"; break;
				case "Increase text size": translation = "Increase text size"; break;
				case "Decrease text size": translation = "Decrease text size"; break;
				case "Word warp": translation = "Word warp"; break;
				case "Status bar": translation = "Status bar"; break;
				case "Font": translation = "Font"; break;
				case "Language": translation = "Language"; break;
				
				case "Team": translation = "Team"; break;
				case "Allow connections": translation = "Allow connections"; break;
				case "Connect to": translation = "Connect to..."; break;
				case "Send a message": translation = "Send a message..."; break;
				case "Disconnect all": translation = "Disconnect all"; break;
				
				case "Help": translation = "Help"; break;
				case "About": translation = "About"; break;
				case "License": translation = "License"; break;
				case "Copying": translation = "Written by: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015-2016\n\nDistributed under the GNU GPL version 3"; break;
				
				case "Size": translation = "Lenght"; break;
				case "Line": translation = "Line"; break;
				case "Notice": translation = "Notice"; break;
				case "RestartTranslate": translation = "The interface will be completly translated only once you will restart the program."; break;
			
				case "AskIP": translation = "Connection to another pad"; break;
				case "EnterIP": translation = "Enter the IP address of the computer you wish to connect to:"; break;
				
				case "SystemInfo": translation = "System Info"; break;
				case "CommandLine": translation = "Command line"; break;
				case "EnterPath": translation = "Enter the path to a program to execute it:"; break;
			}
			
		} else if (language.equals("French")) {
			
			switch (textId)
			{
				case "File": translation = "Fichier"; break;
				case "New": translation = "Nouveau"; break;
				case "Open": translation = "Ouvrir"; break;
				case "Save": translation = "Enregistrer"; break;
				case "Save As": translation = "Enregistrer Sous"; break;
				case "Run": translation = "Démarrer"; break;
				case "Exit": translation = "Quitter"; break;
				
				case "Edit": translation = "Édition"; break;
				case "Undo": translation = "Retour"; break;
				case "Redo": translation = "Refaire"; break;
				case "Cut": translation = "Couper"; break;
				case "Copy": translation = "Copier"; break;
				case "Paste": translation = "Coller"; break;
				case "Select All": translation = "Tout sélectionner"; break;
				case "Select Line": translation = "Sélectionner la ligne"; break;
				case "Find and replace": translation = "Trouver et remplacer"; break;
				
				case "Option": translation = "Options"; break;
				case "Increase text size": translation = "Augmenter la taille du texte"; break;
				case "Decrease text size": translation = "Réduire la taille du texte"; break;
				case "Word warp": translation = "Retour à la ligne"; break;
				case "Status bar": translation = "Barre d'état"; break;
				case "Font": translation = "Police"; break;
				case "Language": translation = "Langue"; break;
				
				case "Team": translation = "Équipe"; break;
				case "Allow connections": translation = "Permettre des connexions"; break;
				case "Connect to": translation = "Se connecter..."; break;
				case "Send a message": translation = "Envoyer un message..."; break;
				case "Disconnect all": translation = "Tout déconnecter"; break;
				
				case "Help": translation = "Aide"; break;
				case "About": translation = "À propos"; break;
				case "License": translation = "License"; break;
				case "Copying": translation = "Écrit par: \nAlexandre-Xavier Labonté-Lamoureux\nDroits d'auteur(c) 2015-2016\n\nDistribué sous la GNU GPL version 3"; break;
				
				case "Size": translation = "Taille"; break;
				case "Line": translation = "Ligne"; break;
				case "Notice": translation = "Avertissement"; break;
				case "RestartTranslate": translation = "L'interface sera complètement traduite que lorsque vous aurez redémarré le logicel."; break;
			
				case "AskIP": translation = "Connexion à un autre Pad"; break;
				case "EnterIP": translation = "Entrez l'adresse IP à laquelle vous voulez vous connecter:"; break;
				
				case "SystemInfo": translation = "Information sur le système"; break;
				case "CommandLine": translation = "Ligne de commande"; break;
				case "EnterPath": translation = "Entrez le chemin vers le logiciel à exécuter:"; break;
			}
			
		} else if (language.equals("German")) {
			
			switch (textId)
			{
				case "Help": translation = "Hilfe"; break;
				case "About": translation = "Über"; break;
				case "Copying": translation = "Geschrieben von: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015-2016\n\nUnter der GNU GPL version 3"; break;
			
				case "Size": translation = "Größe"; break;
				case "Notice": translation = "Beachten"; break;
				
				case "SystemInfo": translation = "Systeminformationen"; break;
				case "CommandLine": translation = "Kommandozeilen"; break;
				case "EnterPath": translation = "Software starten pfad"; break;
			}
			
		} else if (language.equals("Russian")) {
			
			switch (textId)
			{
				case "Help": translation = "Помогите"; break;
				case "About": translation = "около"; break;
				case "Copying": translation = "написано: \nAlexandre-Xavier Labonté-Lamoureux\nАвторские права 2015-2016\n\nРаспространяется под GNU GPL версии 3"; break;
			
				case "Size": translation = "размер"; break;
				case "Notice": translation = "уведомление"; break;
				
				case "SystemInfo": translation = "системная информация"; break;
				case "CommandLine": translation = "командной строки"; break;
				case "EnterPath": translation = "Путь программного обеспечения для запуска"; break;
			}
		} else if (language.equals("Chinese")) {
			
			switch (textId)
			{
				case "Help": translation = "帮助"; break;
				case "About": translation = "关于"; break;
				case "Copying": translation = "书面 \nAlexandre-Xavier Labonté-Lamoureux\n版权 2015-2016\n\n在GNU GPL3版本下发布"; break;
				
				case "Size": translation = "大小"; break;
				case "Line": translation = "线"; break;
				case "Notice": translation = "警告"; break;
				case "RestartTranslate": translation = "该界面将充分体现直至软件重新启动。"; break;
			
				case "AskIP": translation = "登录"; break;
				case "EnterIP": translation = "输入IP地址连接"; break;
				
				case "SystemInfo": translation = "系统信息"; break;
				case "CommandLine": translation = "命令行"; break;
				case "EnterPath": translation = "输入到软件运行的路径"; break;
			}
		} else if (language.equals("Spanish")) {
			
			switch (textId)
			{
				case "Help": translation = "Ayudar"; break;
				case "About": translation = "A proposito"; break;
				case "Copying": translation = "Escrito por: \nAlexandre-Xavier Labonté-Lamoureux\nDerechos de autor(c) 2015-2016\n\nDistribuido bajo la GNU GPL versión 3"; break;
				
				case "Size": translation = "Tamaño"; break;
				case "Line": translation = "Línea"; break;
				case "Notice": translation = "Advertencia"; break;
				case "RestartTranslate": translation = "Reinicie el software para traducir la interfaz completamente."; break;
			
				case "AskIP": translation = "Conexión a otro"; break;
				case "EnterIP": translation = "Introduzca la dirección IP a la que desea conectarse:"; break;
				
				case "SystemInfo": translation = "Información del sistema"; break;
				case "CommandLine": translation = "Línea de comandos"; break;
				case "EnterPath": translation = "Introduzca la ruta de acceso al software para ejecutar:"; break;
			}
		} else {
			
			System.err.println("Language '" + language + "' is undefined.");
			System.exit(0);
		}
		
		return translation;
	}
}
