// Copyright (C) 2015-2016  Alexandre-Xavier Labonté-Lamoureux

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

// How to compile and execute: 
// javac -encoding UTF8 Editor.java
// java Editor

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;		// import java.util.Arrays;
import java.io.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import java.security.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64.Decoder;
import java.net.*;		// Sockets
import java.util.zip.*;		// GZIP streams


public class Editor extends JFrame {

	static Editor mainWindowReference;
	static String language = "English";
	int tabSize = 4;	// Get the default tab size			int tabSize = textarea.getTabSize(); // 8
	String defaultFont = "Courier New";
	int textSize = 14;		// zoom
	final float textSizeIncrement = 2.0f;		// zoom increment
	boolean lineNumbering = false;		// lines
	boolean lineWarp = false;		// wrap
	boolean statusBar = true;		// stats
	Map<String, String> configs = new HashMap<String, String>();
	String lastSelectedDirectory = "";
	Stack<String> undoList = new Stack<String>();
	Stack<String> redoList = new Stack<String>();
	String lastCommand = "calc.exe";
	String lastIP = "127.0.0.1";
	
	// Networking
	int port = 8166;
	Socket sock = null;
	BufferedReader reader = null;
	PrintWriter writer = null;

	public static String GetStringForLang(String textId) {
		
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
	
	public static ImageIcon GetImageIcon(String textId) {
		
		String b64string = null;
		
		switch (textId)
		{
			// https://www.base64-image.de/
			case "iconInfo.gif": b64string = "R0lGODlhEAAQAOYAADVXh8/k/7CzuHCSwFqBt+bm5r6+vsvT3ktrl4+qzvX4+7vL4Y+huKe82ODn8ebw/2iGr8XW74KVrp+uwV58p5201IOhyczMzNvq/////+Pq80Bhj2qNvu/2/4Scvtjh7qu91WSJu9bn/+Hh4czY6X+ex9/t/+3y96Osufj7/8LCwneOrrjJ4KG31lZ0n5OjudDb6o+oyl+Ds77O46W+39TY3G+Mtcnd92B9p+ft9eb3/5Ot0PX5/6zB33SVwr29xYKexaCvwoWiyqKsupKjusva7////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAUUAEYALAAAAAAQABAAAAfUgEaCRhotPgQhQjODjEYNHCwaGQowO0AkjRU7CikdnjwZMBwggy0tGR0PJggARQ8pOSEHhAOdJrcuCBEmrzMeRhULKSYYxcbFHRkhNQM5HcYxGwA9IiIPGTtBBCkP1dU409UmKQ0MIQrdAergNOrjLQwlHx0i6gEUAO0BJhklKDM7Mpiwh6+diA4nCKgwQsABDwwBbEiDcONaAgkXjCzgcILbsWQNVhgoIKhFiBkZMqRIkeFEghUCRjA64IHDjhYVLMiQYEBmIyM1JhB5MUTFBZKCAgEAOw=="; break;
			case "truetype.gif": b64string = "R0lGODlhEAAQAOZ/AEdchevy+ZanuKjL3vH0+ZiouOrx9+Xt9ubt85Klt8fj/sDf/Vt6np7O+0FkqPT3+/P2+fL1+FlujJSmt97n773d/bXZ/D9jqK7T5T1hpykxQ0xZcUlUaU5eeT1EVUJLXf7+/qnT+8Lg/bLY/Lrc/K3V+7fa/JSjsaLQ+8bi/ZvN+6TR++32/r/f/ZbK+8Th/bja/bDS43qPq1BpjOTr9bvc/Oz1/o+gsUFIV3yjwOLp8LLX+8nj/b7e/XSVsYOpw7ja/Ja805/E2JChsdXb5JeouOPs9+7w8m+Or5fM/+jv9VVphufv96/X/Iq9747B8YOz46bR+/X4+fH0++Tq+JLI+6DP++Tv9XmbuFlgbmeHp+/x9OHp70VPYVFhfq/Q4srl/pmmsqfS+46jt0BHV0VQY9Pn8Nfn8OHo8OPq8brc/eTr8rvd/X2hvK3W+6/W+1VnhHSXtfb5+4OrxnmeulJjgLnb/cjk/oyyy4y0zezx98vM0e3x9zxhp////////yH5BAEAAH8ALAAAAAAQABAAAAflgCB+g4SFg3+IfmYYXwNCQXg/bVg+Wn6JGGdXBkwBBgegRk8Ml34xSjxgdykvCy09LE5QEoMDDwW4AhMTFWwsNhBwg0JyYQpFAgsnajAmIxFeg0EPKSkCAA59F9slER2DeVMvIhMAdtpNfSERG4NzBCILCWMWfRkhYis0HIM5ei0nEgy5YS8KCitUugyiw6cCCRM73PTpg6KBCjQfBsXRUwOIhTcTJyZxQYHMICRSHo4oEWKFFRUuqmzxMIgBEQgGECBYk0YHFwpH9mgYJGOGhCV1OmzgUOYDDg8asvgRZKjqIBCBAAA7"; break;
			case "bubble_icon.gif": b64string = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAACXBIWXMAAAsSAAALEgHS3X78AAAA5UlEQVQoz8WSPWoCURSFv5HpsojUqd8SbE0jQYiVCL6kcAGSBUgQ0k9ir6BWamlKO9FOtPMtIUUgDk5OijAGo0NGU3jgFPfnO7e5niSee6+6f+yQVkGtwN1N1iPojoSxmsxXSqPJfCWMVdAdiWPA3wEZAHN1yTGK9/20wDra7/kA7yEnyQcIo3/AH5szXM5cfG6YLhxhRGpPF+4bfnookq/WmS0d64gdv4U/jnuzpSNfrRPUCnhJ71m5zWGL19v6pTWg2R7uvqekg8ZYlRt9jZ1UbvSFsdrbSYLjgFIC+CccByTNvgCnDUaOQrlUIwAAAABJRU5ErkJggg=="; break;
			case "close-icon.gif": b64string = "R0lGODlhEAAQAIcAAF4EDGoAAHYCBoMAAIkAAJAAAJYAAJ0AAJ8HEpwaGokgIIojI4omJYs4N5EkI6MAAKkAAKsCBqkJCaAcHLAAALUKCL0CBrQTEZVDQadcWqhiYcUCAMcFAMkAAM4XFdsDANMVANEyKNYyKN02NuIMAOURAOkVAOwaAOYdF+o1J+07Kew/LfU8PO5NPfNQONpdTtloW9tyZeJBQexHR+9cTe5VVfxTU/tdW/9oaP9wcP94eOBJ8fSBcfeMcPaNe/eNff+AgP+Li/maif+ggP+kjvunlfWrq/uurP+urvyyofu4qf28r/69rfW5uf+5uf+9uf3Bs/7Guv7KvfXHx//Bwf/Cwv/Fxf3Ixv/Jxf/MzP/Q0P3W1P7V1P/U1PXu7vXv7//j4//l5f/p6f/y8v708f749/75+P76+f/+/v///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAADsALAAAAAAQABAAAAj5AHfsQGChg8GDBi0gEDhQRI8hECNC7CFiIYIQRLCEEcOxYxgsREIgiOCDSxQpUpioZIIyChcfESo8WQLlzBkoSZLUvLnkCYULWoqQSZPGjBIlZoiSKaIFgoQsQjB8IVqmDNEvGIRQeXCgyo8YDbygGYvGS4MXLoIcMOCEBw8YGsbIHZMBxIkTQAwUQEJjRYstYAKD2aLChAkdBQgAKZHiSpcuU6Y8vpKiRA4CA3CQOGLFShMGDJp0PkLixgABNVA4MGJkAQcOC1g7QFFDAIAJNjwoULDhw4cNuz3YmABgB4AEM2SMYMGcxQgZMxIUFwgAQIDr2K9XFxgQADs="; break;
			case "exit-24-000000.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH4AoTAisQus9okQAAAs1JREFUOMt1U1uITVEY/tbea+2zL+cMY5DjMkM01DyhyZgHuQ2ShgfNxNt4QC4lJOWNUnghD0TKJUIowuAonqSU0tRgGBrTMJwzcy7budh77fV7mH3MmRpfrdbl4busr58BIIwgxYASAdUAGAAZ7gqAAUCunDV9UWLz+p+3Pn4xYoYgAAEPCRgDhizOT66pm/HM4lz1ZnNRIrCYIfwPw5ma7/nC3bzva9qEWEAgFYoqLVQBAfMLUl56/LX/em8213i2dW1uz8KG5P2Na5K1VdEsABU40Tza2mWkdo7PgDQAV8MoCIDylFrWlRq+dvHVm7jr+4guby75akTILBbMV0ePWzOyKd0LFDQGVBKwshsAkERs/dzZxaqtu/f1/S5MZEAqYplec/Niv2lZk5f2PBT9AKziE//B4vrvFQvqF7/88GlXQcrtc6pii4SuYWbUKRi6RnVVMe/8u+6fl9ct/2d9zBJcT0cFvxHePQBZAMMAMgCKAD7bnIO6EuD4D4hIVEQzwjrLZ0EgQKnxCXQCX9VQfyTR3ZMsyqCjNuY0mpwHNWbE45pGccf+cytxA9d2Hh6fgDGwahnEXrS3Hlx9++FATpHdn86emTWvru353m1DEJyu7jgMm+tjWhiTIGIa/tvBpOleOHFqrmO7BMShCCACNA1RwWELAR5mAyoqLcoAnZ++6j+m1Didx047yWIpAkDLF0tWvrdPu9PzxZhsmaYikryi/0oip9/NP+h380H4JgDA1HXfERyGrlO5ezbNtjanSqUDUlFTeS4AuHHHblkwaWLS9TyhMUYW535HQ31mmmOTFwRgjDEAkiW2brIjjLEt95+2/CqUDvlKLQXgLpk3u+H1oyu/np27KqbGogQ3r/34NsCDIBgZvXBiedfAoGkbQt7c0NI52TKfLLl+b0PO8/dnhzMR3HzAet691wcNQYwxTIgYUmcMNOpU/gXkHDd3k8od/gAAAABJRU5ErkJggg=="; break;
			case "c02228162.jpg": b64string = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAUDBAQEAwUEBAQFBQUGBwwIBwcHBw8LCwkMEQ8SEhEPERETFhwXExQaFRERGCEYGh0dHx8fExciJCIeJBweHx7/2wBDAQUFBQcGBw4ICA4eFBEUHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh7/wAARCAAUABQDASIAAhEBAxEB/8QAGAABAQEBAQAAAAAAAAAAAAAAAAYFAwf/xAAoEAACAgEDAwQBBQAAAAAAAAABAgMEBQAGEQcSIRMiMUFhFBUWM1H/xAAZAQACAwEAAAAAAAAAAAAAAAACAwAEBQb/xAAgEQABBAICAwEAAAAAAAAAAAABAAIRIQMEEmEFMlHh/9oADAMBAAIRAxEAPwD2raPUvI5bq3lMFFG17GzZazjKqKixCgtCCNrMzsfdIZJrEUarwAApbn57qG/1T2udo2Nz4K/FmsbUyUWPtzVX/oZ5khL8Ee8AyKRx4ZSCvI8644TplTx3UHIbnFoRwvlXytGGtH6bRzTVxDaSRiSJI5GWOXgBSGQck8alOjO3Nr7n2zuqFqdcmPeNx5/R9hlMNozV45SvlkUsGCnwOfz56Hab4/I0ZsbTDRjBjueU0LgASIBN3MpLeYo9q16Mbksbp2e/7lcW1mMPesYjKSpD2CSxA/aX44AHepR+F8Dv4+tNaHSvaX8M2hFipbMd3IzTy3MldWFYzbtSuXkkIUD7IUf4qqPrTWTvnC7ZecHpJj8oQPgigmMniJVYfjWNtjbOB2zFaiwOLr49Lc36iwsK8CSQ/LH86aarBzgCAaKJbOmmmhUX/9k="; break;
			case "network_icon.jpg": b64string = "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"; break;
			case "icon_disconnect_agent.bmp.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAIAAADZrBkAAAAC80lEQVR42p3TXUhTYRgHcO9EhwOVmSZ+5PwIiQQLDEwCDYXwpkKLCkTz1jU3urDA/MiZKZJTCTMyxOk2OXPqdMzNbS6n+9ApzOnc11HP0dNcui3d0Y1yvcIBiRKihz8v7/PCj/fmeUJ+/FeF/PlkWjXrFwxyuWpG9UWpVBoMSyaTyeFw7O3tHR4ensvU8zqRaMpicWwhmMu1v7kJzgMUdem0CzKZYmXF9HemNyypVBrJlEKjMcA29MDrOzw4PjrygyDItlgsJRiGOcUShWhCPilWgExMykEAk8lUcvmsUjkHwygwx0d+FMEmJsQE02oXEdQJb2y73b59t8/t8Xm9OGhBrLYNeANVa4w4fspsNkQgEBFMpzdYbXB752Ire/liWucFau/lHFEf91s/5B0YwS3WjRFI5Ds4ZWYzDEEjBFOrtRYbXMUU0V/Irud9pFxix6b1vWRZ6tqRRrZz3QoD5vUcA2k0rvJ4AoIpFbOA3X3Qe//xQEFxPyX5DSWlq/iRooJurGSur6w6BNC466vXs4/rdMtcLkSw6WmVxQrfyGvIzW+7ebsrNqWBktx6NXewqGT64R0O/0lZPyWGExs3fK9E0ts3NMQjmFAoBuxWQX1ufnNOXlvqlUZKUl1SZmfRtZbX5NSFhARTZqYjI2M3JuZzBPldeSXB+Fyhzb61araDrJntzl3v9o7btGaHaNWGuLjvwaC+5a2K9gxcluLjX1FTCWY2W0VjEmh4DHjuoHBgYJzHHR0aHPkUTjJQqbMMJgAgOzU1eFYWJzz8bEoCgUBVVTWNxqDTmWVlTxmM57W19ZzQ0EUqVc9iAbMZDC4Clp4+SiKdMQzDeno+zMyoYHhzG8OMRqNUKntfWChJTATGUl4RYJ7+iZLJ48XFZ8zv9zc1NWm0OgRFf56cBINBj8erhiBOCnU+LMwcHb0eFWWPjJSSSLBQ+Nso4zjO5/M7Othd3d1sNpvFYjU3N8/xeJOlpZMREWoyWZedjchk5y7Ov9Qvcu7ZQLq8obwAAAAASUVORK5CYII="; break;
			case "netico.gif": b64string = "R0lGODlhIwAjAPcAACtQKyuq+zS5+0ax+0a5+09QT09QWE+5+0/A+1hXWFhXalhXjljI+2Ffc2FmYWHI+2pfK2pmzmpuampuhWqx8mrP+3Nuc3NuhXNuxXN1c3N1hXOT13PP+3x1fHx1vHx9jnzX+4WEhYWEqoWMhYWMmIWM14XI6YXe+45uK46Mjo6MoY6Ms46MxY6Tqo6bzo6izo654I7e+5iTxZibmJiboZibvJibxZjm+6FuK6GbmKGboaGbzqGioaGivKGixaGizqHX6aqi16qqqqqqzqrP16rt+7OxzrO5s7PAzrPA17P1+7yTK7y5vLy5zrzAvLzAzsXIxcXI18XPzs7Pzs7P19eqK9fX19fX4Nfe1+De4ODe6eDm4OD1++nm6ent6ent8vLt8vL18vvmmPv1qvv1+/v8+wEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQAAACH5BAEAAP8ALAAAAAAjACMAQAj/AP8JHEiwoMGDCBMOjGICAYMHDxgg2GBhikKFWr6UCWNFyIcPDS4ocBDiiJcyBDOWQTnwCgwBBGIeoLAhAYCbEKpUQXETQIGXMQnMpGhxYBkvU47wWHrEChiWB48mXcqj6dOLA60YMXGjiBIlRWJs6GAFK9YnT4JcGcLCA4sWUMhg9WKEw4muRW6cIDpwBhcAFenaxauXr8AnVMiE6WLEiAgbO2hkgUoQsWLGjiFLNjvQS5YrVBoP6WElDOeEZYxQsHuiNQjDpw+SeREgwIANGyIYCIEl9kHEXjw3oaKihowOUCgf1NrYSBIiQG5s0LFFYJkZ2IV0YW4EyfPo06tb/99aoTyH8hsmYJ+hU+d68ubRVyRY5gfu3BEWSGDSJcsUJilkkEEKTEyRhX24YZDffqYJlAUJRvggoQ82rKBAB1Mo5yCEE1JoIYa+hSjiQCpp6Bt33kEnHXUjdlbXXUXktdd8Lf6TmgnlVXBeBbC16BJMMtFEY4313aegfkw02CJiTyzW2GORTVbjgxF2+GGGZnkGmmikhSFVDgBIkIJV/W1pxGilZbVVV1+FNVZZAvkFQAhZaMWVV2CJRZZRqrHmGmxyVpTaaq39OeQ/DDkEkUQbADCDFE9UIcYYSzjxxAwANPRQRBMdOlttt5WgWwc8jGABDqjiUIAEFozgAqi46UHGW2VprdXWW3EJFIZ/BpqGllpsuQWXXIdREVwWwxV3XHK/GSscccYhNxBHHn2ggQYNkGSSidR+dG22JXlR42kBAQA7"; break;
			case "debug.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAABWtJREFUSImFlltsXNUVhr+1L2dmzjixM74Q24lqB4MoJglpaomARFrRyAElKr0QXGgqpaUqTYT6VFWKeOlDVfWhUqU26QsqlVK1hqI+FEibCqmNRJRUlgpOYiIgYRDEzmXGji9z8ZzL3n2wHce5wC8d7Zd/rX+ts87+1xE+Ay2P/2F7mqRDzqeDPvW9mZYQgMZ0DdFSVKKPaaOHp//x/eN3yiG3TbzryOakUR9ZtbZge/u76OltpdCSI5szAMzXE6am63xcnKQ4NsHc5anYZHID02/sHf1cgcLulw8pbfY/9MQm7ulrJRcaxAvO+RU8pQQvnnot4cPzk5w6ehqXJoenXt934I4Chd1HXlndvWbPE7v6ae8I8V7Ag8gCURbZ3oNfPBEQ8ZSu1jj6xhiz49denXp979O3CLR9e/jQ6vam/UPf2UJzU4BLPVHqmY08F+diqqnDJ4vVG1hlFG15zZrAkDMgWpipRAz/5R1mS5XD5deGDlwX6P7u3zYnafLuvue20dWWpxYlFGcSwNARai5XE+aiCC+CsFA9HhyexMFdoaFntSYMDBPlKi+/dBKjzYPjf/rmqAKIonhk5+6NbOhcRbkWc64cU089aZLy082r+dH9TXTmDHmlyBhFaBShVTRZTUtGU08858ox5VrMhs5V7Ny9kSiKRwD0un1/3x625H/wvV33cX4qolTzBEphlSKJUh7rCbm7NctgTxM+SXj/SoOMFaw2WCUYJQunKOYimI9TtvY089/3yjr/wJ7/KO8ZGtjazVzqqaYQWshayFnBuZTUL0/ryfvW8Juv3cU6C/NzdRSOrBWyZiEmtFBNYS71DGztxnuGlPMy2N9XYHwmIVSQUUJWCaERjEtwzq34PJtDy4tf6eLgw21cGp+jeq1GoBQ5rRbiFIzPJPT3FXBeBhVCr+RyBFqweuEdW6O41BDeOj1LI1kpsIS+9jyv7b2XR7uzHD3xKbMzdYzVBEYTaEFyORB6VVgICQJFYIVcINRSz/+uxJSrCS5OkJsu2M14dutaTv/sQWwU887oJbJGCKwQBIqwEKJskCWrNU3a8NGs58K0JxdowozFxyn+tmZyMxQT5Qqj5yYpVh1N2pDVGm0CVD1x5I3mQiWhGnvyGVkYXCD4pIH3n93Br/51nvyzb/Jx3bDjG5uoNTwXKgl5o2k4MBmti2dK9d5qBKFV1wOtVqSNFJ/eXuDEB2V2/vxtuvu6GPrhNjSCSxxZq6hGcKZUJ6N10ZisPfZJqfZ8Z0fTCkMLAo1P3KLhLOPiVIUdvzjFdN0z+NSX6e5sJo5TvPdovVCgUsInVyuYrD1mVMYMX7lceX7D+haiOF3uwGrSNGLZrjxf//VJ/vn2OI8++SV2beoijmNc6jBauNE3A6u5crmCyphhdfbgluOVmfl4ttogEyisEawRAiO4BDCO3731IbLjj0wQ8sKLj7Nl01p8GqOF6/ylJxMoZqsNKjPz8dmDW44bAB3YgdGx0ruPPbwORPDeEwjkc4ovPvNXWu5Zz09++S2aM4Y4dnjn0EsefgNEBBSMjpXQgR3gxr4GfvveoZb2cP/2jR004hQQUu8pXavzha5mokZ8y9JZmRwyVnP8zFWmS7XDIy/cf2CFAMDA7z94pa01t+eR/la887hFgsffPOvlxIASQZRwYmyS8mT91ZEf33vrwlnCQy8VD5nA79/2QDvtoSFxHudvFRBZSGyUUKolnDxbIonk8Knneu+8MpfwyJGLm5M4Him0ZOzdXSEdTYYwo69fOhGh1ki5Wkm4MFFjaroRG2sHTuxd9/lL/0Z89c+fbk8cQ6mSQZzvzWY1APPzKSgpauePGcXwv59Zf8fflv8Dg0tHhfkv0+kAAAAASUVORK5CYII="; break;
			case "WordWrap.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAABhSURBVDhPY2AYFCA+Pv4/DIMchMzHJ049x2OzEZcrqGcrsknY/ElMWFDPNfjCANklEBv//0dgot3wTx5TKcggggCk8W8zxEZ0QNAAkGZk52JjU+QCgpqRFWALA5IMoKFiALpnfwxKe4MuAAAAAElFTkSuQmCC"; break;
			case "ui-status-bar.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABPklEQVR42sRTPWqEUBAefSZBVEhpERZkwco2lQhpcgELj+DeYGHZOgjb5SRW3sAjeIJU24hFYuHPauYzWTFsDCQWGRjmvZnv+957zij1fU9LTKaFtlhACYJAj6LI3O12R37O27ngOM4XYJZl41qSJH27fTIPh/1RbtvW5Nw94ul0IrhlWRcnIXeuTzkQWPHmEZGd1us1aZp2IYAcasBMORC4480Dom3bpOv67HtRA2bKUZqmucUNEVVV3eCEn4wxxNibM0ep61qgEIbhs+/7hLlI05Q8z6O5GYnjeIjgQmBoJatRURRUVRXleT6uvzNgPwXk8QZIJkkygqbrORtuUJblFfeVXNelrkOL4A0LDl+bcx19vERw/5XBhbjGLNDANQxjI4RY/WUKeSZeJHxYdgNT+Us+2vUq/fvf+C7AAC80vJSqj+gnAAAAAElFTkSuQmCC"; break;
			case "nav_zoomout.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAmtJREFUeNqkk89rE1EQx79vs9ndJJuk2bSktdimbazRqFkUoYLSHBQFQULxoh7cg949ak+i0HP/hFQQrFjSg0I9GbCilVLSKlQRa7TpD21t2sQkm83+8CVUCESE4oP3hnnMfGbmvRngPxdpVGLKEy8ViuAX43YnL5u6sa0V1HT1lzqZSlwe+yeAOkcFSUzEBkNypL8VPT4BRc3A1y0V07NZvHuzlLJ0I05BO40A25/Igs+VunrlRPhkvx9ugYVBCEzCwOtgEQ35UeK4YDbzM5yZGx9vBDC78ta584eC4Q4RHGeDJNohOVk4eRsqNhZli+DSqS70Hu2M02CDTQBnwBuXaRQwBE4K8PEEkkDgYgl4apE3CQxYiB7bB7CM0gTg3A7ZTaPNzrxCpF1EwOtCm8eFcEDEmW4P7ly7iFwVCLg5MA4+2Ahga4dlWXWFF72QB07XEqkvk14bdLeGItDNmmHdGE2A8mYhnS/rcu/BIxh5+AyCjdS/R6Xe25qJTEGH1w58zpVhlrVMUwnaTmly/tMmXIyFvGpgtWhgpWRgmcol6hx0ENitIhZmn9OUzMTffmF0amox/TGbx34ecMBEqUKfzTAQdTPocjB4MZ2EZ30Z9zuG4019kEk/rnQdHnq08OHHhQpIe6BFQF8LB5Hiv60V8HRqAuriMm7G3qOQKw6c7RMzybmt+aZW3m2q64RnFcLZ6w9mavo2NH30XvuwYpM8ir/Tg5mXq1jbqCi3J76Mkb0MzoMboWT3ASneJvG0pO/YKVQVZi+AfNlSXr/dSK9ki4gd99WuFLLX8R0Z6qlNbMopMHJJNe/+FmAAKnzjx6E0T9wAAAAASUVORK5CYII="; break;
			case "nav_zoomin.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAolJREFUeNqUUktME1EUPdOZMjAd+gmFFgNSWhS0KEaMoqiZ4EIiJrIw0bhxNLpxYXRjkI2YuHBpjJq4kTEu1IiIv7hQDAkKoiKCBjUQ24ofggItdKCf6YxvGiHENgZv8ua9N++ec+899wJpTBBbKvSFRRizAFREs8YmfolNXFlsQ0LVwDmfIDIRltSY0tQh7Q6kI6DmIuYV5XTU7vBal+VxYBlD8jGmqOj9PIn2xx+DU6MhgZD0pxAQsCUr1+w/eqjKmsMxYAi4t+c5MnkLPKVekqKG0VAU5y93BZXpWRchCS0k0EMd21nnTYIzMxnkmBgc2VOHi2caoDE0plQKTguLyk1uK/EV/87AYMq3Cd5CMzkZkqlnZ1B/aqNgJPQRsocTGirLcmHg2PoUApplBC6DRpyiUO7k4bCYkg+vuztRvdSMhn11CMYBK88Sb8qa0gUlGvcTrCtOolRs2Ex8gL4Xz2DKtqB4xSrYS7xQNY0ImtD9gykEkfFwx/DYjFhg53D62gNk0RS2eyxwEfCJ5vv4PqMgn6XwamQa6mzMn1IC6bHU2eUD8QGjqvCHE3CvrYbV7UUgrGA1T4RQZfQ8vY5CeqQt7RzUHL7TXCUsF3dtKUaEDNB4TEuWYieCanEZtx9egm0shq3KrbchGUL9haH5VtL6x/fm5l3aXIOBQFAAaR1H/spyDAND42i514PSiUHs3TgI1cA4R77ItdvKbDcevZ+MzmewcJzJprdqTm1dtLZTjkYhShklV7kDo1+n0f3yl3Sy1XcgheBfJh0s2Q8jI62vtKPv3SQC32bONbb6jhsWSyBeGb7642dU1MFrPDzmppLGf1j7h2D/ugLe/ykg6yWe1e+/BRgAY+fpyOT04WYAAAAASUVORK5CYII="; break;
			case "botao-refazer.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAAAsTAAALEwEAmpwYAAAFQElEQVRIx63Wy29c1R3A8e8599w7D4+HGY8f4/htArFDQkJiQpyH0jStQkLVSCAhgYQoi1JVrapKZYPUdRYVlWgXrVRaqVQi3VTtAkiitCVKoBDAdto4CU4wsYMzjj2e9+vO3NfpAtEKklaV6u8f8Pv8ftJZHKG15suVSjWuL2SYvbL04uW5xePLmQK1qo3rOygrRPyeDnpSCR7a0v+dfbvv+9XgQBeWZd0xx/M1ggDxZeT2aoE3Tr59fOrKrRfrjkkknCQWTWGF4ggpqDQa1BpNqvkCbn2NkQHFwQNj7V87uKsWj0X/vWjFZurvH7NxqPOLyM1MkVdPnNJTM4sk+zbS0TOI9iwaNR+EQkhBzbFxfVCBwrfrrK1cQfsrHH10R/6ZJw92plMJ/ABe+9PFp17++W9OPH1s4va/kMxKjV+88qaenl1h5L6dJLqT1G2bVlOitYkwwA1aNFoNmk2PwDEIGRFE4HJreYFKeYEnj4zw3acPi5lLWX788lt6fnGWw/vjn11SrbX42S9P6bMXPmbj2CPEOrqpVGsoocH3KeQLlMsltPYJxWKE2ztwXEG90UIKhaWi5LO38KtXGU2HyFdCFPwhRPApQx1zKM/XvP3OPH/78AZd/aO0JROUa3kkFpVigZvzH1LKLmKZkki4jbw2CaxO4t334hKjkC8QkhXaIhHqQR9/nlmks6+Tnq40fi5LNFCotVyFk2fe1ZFoknR6kEbVwzQsyrkMs9PnMFhiz2Q/D24dfyOVTB8vle3nz703962Z2fcxItsIZBer5RKhaJNYLElnbwitLNaKJSJOC0+Cenfqo9RytkTv0A6Eb6EwqVdzXL74FiGxyuPHdvPNY3vFwMAI90TTAO8d2DP33K9fPaNP/vUqqm074VgE27GhBlJK7FaTaCSGqTSe9FAfXFzIWdEUoWg7rgsi0Mxfm8FvfsqRx3bzxLGjYtOmzV945pvHxvjh92LC0X/Up89fI5zcjK0FrgtaazANhFD4nkCgkAtLZQyrnUbTRuNSLCyznLnE+HgXjx05cAfweYP9/Wwc6Jv2ah5ogVA+QjuYwkfJz5ZVWqG0Qq6tVZFmBMcP8H1YzswTtursm9w+vevhXfyn/nDybO/rpz/ZaYRHaboS7XtoPFqeg+dLlLQwDQMhDVSjbmOFo2gZpVj0WLn9CUO9MbY/sGVCGsZdgd+9dv6pl145fyJbGSXVO47jVUiGTGQUXOkTDnUS1hatXAPbA+V7Hq4raTQ8VjJ56rUCQwOd9KZ77gqcevMCL/3k9ydmrzfoGE6QW7qA9KtYBggV4JsurpmgUHPxalcZ7o+houEI9YrDatlmrVhES4feDQmSyfhdkVhccfCrw9w7nsXxbyBEgIWLxEBrE1c28OUqnqNoC0XY+mAXKt0VJ1+scDsb4HtgmIJUsv1Md/eGuyL7908wuWebmJv/B3bTRkmB0iADhY9BIF08AtAWhhRYlkT19rRxbSpPsdpG2DKImSHaw7HX+S8pw2TLpgn+1+S27aMvEDi0HI0baIQwCRxzmHVM7n1k009H+toxfRfh+QSOSaXqPbGuyGBvksOHRn6bbK/h2QFNN0y2vjKczS2tHxKJWHzj6APPTU7GwHeo23EWcmVWCyvrhwD096R4/tlJsWNrlKAV4srlMPMLdWtdEYFg77b7+f63d4qHH4qxfKPBO+dvtuxma/0QANMwOHpojB/9YEJ8ZV+aS5euc/ovU+tyzR2/Fcd1+Oj6Lc6d++BsOCSnv35o9wsjw4Pri3xeuVwik8mQSCTYsKHv/0L+CRsJZMZ0irvyAAAAAElFTkSuQmCC"; break;
			case "back_undo.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACb0lEQVR4XpVTXUiTbxT/vePdFuF0BTFW9ufVvMlu+iACka6CQY1gQVdtEmTMpSKzzJT/RTdCRHhT4F0Us8LGVqlo1lZaFslWQWBkN+tDkpSpbfNz797T8zy6DbUbf/Dbec7vfOycMwa0DBJjM7Ko72mBtz+KplCS6Ronf3NNxNZBt2qv4dJzL0uKwGRqU/6zHDqyd1dBk32/xMnfXOMxkVPXXYlVSLjykk4fKIb/4zgUSxEO7zRBKd4Bjm/jU9ys8f2fJoCFhRiWl6pw6+Qw0BymhlfT5Lg/xmycHA++ktL+nsRqrUOrdpBpH6hhKC7yhObti0CgKUTu0KTgcd8X4j4aB2bYvj7UPqkQrO/1cU25ESV3eJJO8LzLIQ11/CYXn5Grf4KqGF19E3Ts9iixe2QPm0dtt5PtP6NcHxF5ZVfDhIbeqMQ6E0hcI4ec327jah513T4YDM5TR/dh8vc0hkfHUxI2gwuPKyDLb2wV5cIdePuZZGwWmQxSSyqICFBVyKgJJkFaQW4Hna4THQ4X/gUiD2+QXEwjNZsASJvTgWgMqoY95WWw7raAJdjheeTEeniCTqgZu2IxswnSmGI3gEZjMiQpAMocTC2nJcm4hU9gRjp9E+6Ajb07wKFpHqRVOzKqedFUhOX4HyRnEwSjMQCB8/4IqnxU2DYiaGnsIe7n2UlK61MWe0dbW18Ijdfk/wuy7IXeEEvEvmM+kcRM4XYYSkohW62ChtIS/NKbWGwO8z9+Anp9TNSsQU2wEtVdEZy5o7Gfi7Z5ewj/vxbkPs51kYhVP4zAw3I3IN+ohSVFcfZeEs67Gid/c03E1uEv5QpTFzvZK5EAAAAASUVORK5CYII="; break;
			case "edit_select_all.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH1gUDASAJP3yDPQAAAAZiS0dEAP8A/wD/oL2nkwAAA6RJREFUSMe9Vs9PE1EQnu3u9te2drc1QEWt1AgxIlC8aSKJ/hskJPAfeDPxwL/BjcjNkzcuevDigYiJELyQAKIULbTaLe223e3Wbx7dDQp2vegj07f7dt58M9/MmwfRPx4S/ywtLcVjsViu0+kMhkKhq67rZmVZHlEUJY/Pw3gfwrcU1kxJkrZt236O+cXc3Fz5rwCWl5e7qVTKymQyrq7rcjKZVAEoQygcDpOqqlSpVKhcLlO326V2u+1sb293Wq3WEuTZwsJCrS/AysqKOTs7m4RnwgA89mfv2XEc4u+e8Ltpmh0AfbMsa2p+fv7oIgBFoEiSCQqSjUZDGLtoGIYhvnkAMMprHGV2fX39CVSe/hEAvJexaRhAtHN8QtWmLTy3WjZ12KjToUd3I2QkYrR7eEyv33+kSr1N9wt3aHJoQOJ8/YkipTeX2CsGODQtevX2nVjERrHGw242yNBi9PLNGqV0XeSmabskKwrrDPUFQOgmEkeRSIRUOUTj4+MEygQl3nzcdOmH06bHDx9QRouIzYlomBRF5ceBvgCgqM6U8AgDYH9/nzgf7D1HcTYPNwd1KlzVqbePVEVmOvW+APDS9pKrACCXy51T9CLZPTJpp1SlsaxBU8O6AMF6MogiHsJjZvzgy2eq1U5Lm8uxV2l+NIlEgkYHU6IQejmSg5LsG5KhP5K7fqHycCpGckgSkRjx8NlzIgXlINQLFc8SfS0e0MnJiW+AByc+n9FIC8tCzzuIHAGelSCKdA+AN/wegWdw7dNp6xnPXqJ0TPXXAwHQ1JIMIIy5XREB54ATy14yfSxsbGJiAjruL+0Ee52+AFDSOIHCG/xxBF5VnfHSf9//3qCDqiTydS+R5KjtIIoSHkVsZ2tzg7zG93vz49k74dOFKS8yK6iKEryBKXHQd6YnJ3xvz0XB7RrgrBvFIXMwY287KIJorxrECf6wseEfLPaW74NoNEqjo6OikqBJLhcEhch1bM5BLRCAKeIGVrh5he5cHyAbANxNq7U61a0mxCJFEgkTt4gEAFUNES4c3n8c1CqM1dVVJ51ON3GzyZqmRePxuJRG9xwyRBL9iuL5lEpHrDNAvV6vwAx31DqkwSZ/Adjb25Nh+EaxWLyFSPKg5BbkNiQPI1nMwNRauEqJHQB4GCIi5gNZKpWYosuQ6lnjPsDi4iIf1x3Ibq+vsKi97wpOcWxmZmYkm81eA8g13GJ5tPYxOMP/KOTwvgm9LWab/vf4CQ6fRxRp9jojAAAAAElFTkSuQmCC"; break;
			case "editor_panel_tab_icon.gif": b64string = "R0lGODlhEQAQAHcAACH5BAEAAAAALAAAAAARABAAh/8A/ysrKzU1NUlJSUpKSkxMTFRUVF5eXmhoaHJycnt7e4aGho+Pj5CQkJmZmZqamqysrK2trbW1tbi4uMbGxszMzM3NzQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAhvAC0IHEiwoMGDCBEiIDAhIcEFDQQIeODQAoIAEwkIcHgxgMcFDRV69CggAsEKKFF2JBkhZQULLiccGFnS5UuXBhIo8MjA5s2UHgcwcODzJ8oAAxK0LAqTAoSnUKNSSAlTQoGrWLNKoFq068uKBgMCADs="; break;
			case "edit_find_replace.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAuHAAALhwGTQIdTAAADuElEQVRIx7WVbWhbVRjHf0nTZk3W2Rd0S9NW0gxb0bZpKCoVRvtFkA11DJ2ODhWcOkHHBKWw+QJDWgY6Bs6tw26GhSF1Y/swQRA/KNaNuthuFbRbxQ+FzlDTljSxSZdzHj/cm5vbLq7g2AOHe+65l///ef7Py4G7bI78pv/gR68CA/8HJJfNte9/74OxYt9ctv3A5iefIhgMUl5eXhRoaWkJEABmZ+dQShGPx/n2u29Gt2zZ/PCFC1//Buj/IiAYDKKUIpVKWWciYi0QRAuCcOnSRTo7HyccDjM9PQ3wq8dT3jQ0dOaaHdNpf7F7LghaNFq0SaARrdEIWmvS6TRutxuA7u5ugoEHCHe0T9xOIsuuXZ/kRCRCbNSQNRxq46WdPQQbA2BGspjJcOz4kVXz4yoG/ubet+k/PMjutzyUupz8lUiyZ+8uDh3ss0jaQyESMzOMXP4ZB+Cr9TOTiN9C4Fx5cOKLCP2HBwn4qtlYV0Ojv4YHA+vp+/g4kehpRAQtgtfrpdbvo6uri5bWVqqrq4tGcAtBbGyMygoP91Z5UVqTTGdQSrPW62Z07AqijVxUVt5DdU0NGzasp2Ktl4aGhtUlGh8fByCncmSyN5mKz5FTmhKnw6wi0EYKEDRlpWXkXEsEGxupqrkPx6EOnuhBgJ3hKNGiEYRDbSQXFvnzRoKcUogIN3OKhdQiobYWoFBZ2aUsTpcLp9PJ5BvraOqF1m2bAE4VlUgpxY8/XWTw2CfMJRfJKUFpzfzCIvveeZ2eF7YbfSBmP4gGEWb2NdLUC+7rm8iO/LDMYWtUxGIxefm13Wzb+gxnz52n87FHGbtyFYBQWws9z28nELjfkEc0WgRE+HsF+MQUAO+HoxywcjA8PLwM/OTAUfx+H1hdrBGwPAfjPLEKuBVBa8cjkgcfPHqE+np/Adj01PDYJKE4+Hj9s7zY95XDxBV7Dj48e+48n3/2KfV1tUYpWt5jkRg9oA3tWQ5e1/s9N6pCxfvg6uWRAzuee5rm5mZj1oiY9V54WsAizO7fSDgKe77MMDEFnlfOsKah3a6KrOyDQj9obdPeGHqCtkWjCUfhlx7w7noXp+8hRIR0OnVbAjdAOp2yqiOvtVWWZk5A+GPyd1uOtNWEpnmAfzDvhTxBaf6rMe+1OTRNEtFWLox3rDPjn2V3zBogY0YheQJlhVRaxh1avnDEHkF2IblwOnLq5I47QZ6fmx8CCvcqhU52AGVABbDO1NENlJjylZjOOEwPxbYyQApI2payk9xV+xcFe0dH0xPIYwAAAABJRU5ErkJggg=="; break;
			case "1398640998.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABgElEQVR4Xo3TzUsbQRjH8e88MyY5iS8UxLZQilgQ8SQGYxZFrz1a1IJQKNhbwYMU+gcoEsFzPHi1XopFPAXUHlpIsiXRm9J/IJhLwCDaxpcZWBZ8SfYDw86+zO+ZeWAV9wwNjywAWUKfjv3CBk9Q63MyA3zDgc0/PXzf+0kikaBer/Px3TjTg2eEmF3cut7GAbGLP6z8wo7qq8/cxLu4bvzn70kZEajxjO7JDME3YbEwwLk62WI395uD3A6NfzU6Oto5r1Xu7n+wlt127x9jCDE/+YKpt+8Jhc+b9eDGbi2sALunnVQvYkRwaHjILf6y9JVWVjPLE4YmSuUiSjRaC+KuGlGCaKHvdT9W0wBtDKVSGSWCKOUWK1GMjqYAWgcYY0gmk2jRtqoLcLsQHTFAt+H7PiKCUspWdyHptBd9B6mxFFpcD9xwcxEC0rwHmmKhSD5vRx5jtO2LDY56BIPneShXOTi/2PnDgNibOQIc7WM9731JIOrf6FwOLEK8iwgqt7XAZGTyJOYuAAAAAElFTkSuQmCC"; break;
			case "mai1444425541180_lowres_en-us.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABQAAAARCAQAAAB3TUQ1AAAACXBIWXMAABcSAAAXEgFnn9JSAAADGGlDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjaY2BgnuDo4uTKJMDAUFBUUuQe5BgZERmlwH6egY2BmYGBgYGBITG5uMAxIMCHgYGBIS8/L5UBFTAyMHy7xsDIwMDAcFnX0cXJlYE0wJpcUFTCwMBwgIGBwSgltTiZgYHhCwMDQ3p5SUEJAwNjDAMDg0hSdkEJAwNjAQMDg0h2SJAzAwNjCwMDE09JakUJAwMDg3N+QWVRZnpGiYKhpaWlgmNKflKqQnBlcUlqbrGCZ15yflFBflFiSWoKAwMD1A4GBgYGXpf8EgX3xMw8BSMDVQYqg4jIKAUICxE+CDEESC4tKoMHJQODAIMCgwGDA0MAQyJDPcMChqMMbxjFGV0YSxlXMN5jEmMKYprAdIFZmDmSeSHzGxZLlg6WW6x6rK2s99gs2aaxfWMPZ9/NocTRxfGFM5HzApcj1xZuTe4FPFI8U3mFeCfxCfNN45fhXyygI7BD0FXwilCq0A/hXhEVkb2i4aJfxCaJG4lfkaiQlJM8JpUvLS19QqZMVl32llyfvIv8H4WtioVKekpvldeqFKiaqP5UO6jepRGqqaT5QeuA9iSdVF0rPUG9V/pHDBYY1hrFGNuayJsym740u2C+02KJ5QSrOutcmzjbQDtXe2sHY0cdJzVnJRcFV3k3BXdlD3VPXS8Tbxsfd99gvwT//ID6wIlBS4N3hVwMfRnOFCEXaRUVEV0RMzN2T9yDBLZE3aSw5IaUNak30zkyLDIzs+ZmX8xlz7PPryjYVPiuWLskq3RV2ZsK/cqSql01jLVedVPrHzbqNdU0n22VaytsP9op3VXUfbpXta+x/+5Em0mzJ/+dGj/t8AyNmf2zvs9JmHt6vvmCpYtEFrcu+bYsc/m9lSGrTq9xWbtvveWGbZtMNm/ZarJt+w6rnft3u+45uy9s/4ODOYd+Hmk/Jn58xUnrU+fOJJ/9dX7SRe1LR68kXv13fc5Nm1t379TfU75/4mHeY7En+59lvhB5efB1/lv5dxc+NH0y/fzq64Lv4T8Ffp360/rP8f9/AA0ADzT6lvFdAAAAIGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAENSURBVHjajNI7L4RhEIbhcUyEkOxup5JI9DqUOiqdQqekvX6GrSQsQiRUaAiVU0FHodHwMxRCMYr9FmutyBSTed87c3hmQggVNSmtK4smGzRu2rCBengk3buTDpuwkhMpzRsNw2akmhBq0uwnNuRYSqtChDEL0pwQ5qT5Aut3JqWVehwqpqQrJWWX0oQQ+oqiK438dVeV3r1KaV/ocV4U7WwGOy15cuNUSnsqtqTtL6wBNqxbVUprKhaNfP+LH7r1enDt0e6P918zvnqRdnS0gl2WPbstBNm0WfiWHqvSWzH1nhAOWqeu63ippORCmmyn4783833XG3/tunE9d+6lg/bXE0LZeiF023v8GACYxQ2nFgJgvgAAAABJRU5ErkJggg=="; break;
			case "icon_copy_n.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAIAAACQkWg2AAAABGdBTUEAANbY1E9YMgAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAADESURBVHjavJLZDcMgDIabij1iJgmZBG8SMwl0ksIkZBPqFEJpjqp96S+BfMj+ONyllC6/SPAyxuwT0zQdVzCBiFK0qZG1dhh0OpIodYC1RQaOo2zJFShqSBEBgAJY0rrftEBE5xzb1xr1RA5RwRaY5UhxzSmhdVEpdpH8snNNufSqGO+t24pTWutTQmeM7vuSWjlvR+I78D7P/haez33yD38jhBDWSPcVgWchG1LKMjIfCJs5439tgC9lnjhsU4F7PQQYADyHoT3s+THxAAAAAElFTkSuQmCC"; break;
			case "save.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAUJJREFUeNqkUjtug0AQXUIkXFocIxABbpwbWMpJ4o4maZwbpE8qJF+BJpJLR3FrFMkcgQ6hlGb5Zd5Ii0zsECyPNLzV7rzH7LzVmqYRl8Q1PpZlvRLMDcMYRMrz/IXgKY5jcYWNuq7ni8UzULiuxxiGYZumaXJijTPbth8J38BVAmK9/hBlWfIamCRJm2macmKNs+n0DnUP7RWqqhJFUTBCAJhlWdsyiAjs4Wy3ixl/CUhGDBWIP6kIgoARe4e1HQEpJRdsNp984Pv+n0NEbUcARClz4TjOIBdQqzpsOyBrzvL/qAMIvK9Wg8j3s1m3A3WFigYYbbe9ZG8yOX0FHiJZeOu6vQKoORqisqagDr6iqFfA8bzTNuIhFaR+848TqFGPjgMPR9f1JWVzZi7B1fijaSPSGlOOBrq4p/wm7v5HgAEAzDMjMz77NxsAAAAASUVORK5CYII="; break;
			case "Document New-01.png": b64string = "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"; break;
			case "Open_file.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAEJElEQVR42p2WW2xTdRzHv2dtV+hWdjtj6wboxiwTmSYGsgWHeHtgCLIRl6Ax3h588MlLiERNDL4o4cE3efCBxAd9MQyjFEM2GLDELmDQbWZqV7OOXeq6dr2e03N6eo6/8z+na7t1D/Sf/Pq/nXw/v1tPy6HM8eIPz/RxUtgjSRmIkoyYZD06+d4/V9c/x5UjfuLyC33WTNjT0bYDTqcTkizDM+xFXLIc9Z0JXG35pM6rKJnu5XNJrizA4NBBb1O9o3s1lsCje3YzgCiK8IzchSCr4y18dffMXAhlA04NHdDSSsV4KmM5i3TEc/ipJxGNxZBKivjLfx9NfC2ue6fLB5y8fPj8pf6bp3P73gvt2pHnerCwEIQgpimiTDHg9y+d4cqapnolGS0paHFsg82x6233W6MXS90fvOD2tjZWdz/R1QnfzOxGwMR5Xuv64Bsgfod2FRsVrBX4+/vvxjrfmTm0/ur5i/tI3NntfqRt8wgY4EQrsPpH6b4ipu9XW0Ct6uHk8PSuwqsPG3jWpmJatwzSJC7JWciKOh46l+gpALQAsQkClCCoGuA6BvB9tM6Yh1rhA+xz8usv0LD/3Y7WZz/35242AsoYkmiBUPUa5q9f0bdZvWzmlTsP6HcB0clNRTRyWIjbIMSsbE5EbNDUfLRb+SbYa+tgr6mBIsQRnp7Wj5vzgIFmqsEUshkO0WU7xISNzIp0ylIE2tq4E/b67bDX8SRWy2xLzTaCyZQpCZwmw//zjxBWwnj89EpBkQdaoK5M4M/bvCG0/WESaoG9wUUijSRIorXVLBSOhDRVANKL4JL36ExdcyCRaMbs8Ii+7CLAVB4w+BjEf8cw53NjzxufQuMqwWUjrMAat4VmkfYxIBMmixizkiJxuShC/+2g7v0iibcWF/nVXgSujcC24xhch/pZqNAF1bQhlE2SJWitz+S9HkE2XdRNiXgjZkdG9SVPgHAx4PXjmPz2J7QPfgyHayc4ZdUQ0cVVE8DEcwCRTCn2/tYShHDER+LuDW2699QR+K7cQeebn5FPlGclYogoBeIMkDLONcloLXPEo3UI3BjTl9UESG0A8HvboVXupvScpFxHjXSwNNCzSsKEpAzvWWqKvZ+5tQgxvPobie8vPF8D6HP7yx/B0fwQeR/M55mJmxAGFFk7Fo74qhOBUa++rCRApiTA5qyn7jlLxdWLGjFFczk312uFVYu9vzkPMRK7S+IH1n9BGWDqK5dWt+9puHpfMtKjxA1RlhYzAmYSa8tYqBKx+yGklkNQxHROy04AuSSAIlDbBt7nqlopPXLQ6BoGEZCM0KthaRbC8gJS/y3Ru24tA/Nk+svnGtmw3qUE0DYDaB2vnKEflWoS8yG16IcQnGNmjnEy/R/DLyQyjgcYRUU2xxDZJTIPiUUeRKzU+B+2G2JVAiC5IAAAAABJRU5ErkJggg=="; break;
			case "saveas_icon.gif": b64string = "R0lGODlhEAAQAMZAAP8A/wAAAB8ubh8yiSMzeylBiC8wMjAwYDA4oDBAoDBIsDBOlDZcoUBAoEBIsEBQwEZGRlBQsGBYsGlaNnB44IdLSouGgI6GiJCI8JCQ/5OiqplqaZ5yPqB3P6CY/6Cg/6OdjrCGjLCo/7y31MC4/8DA/8RyccrE4NGpU9WIhdXV1dfX19zc3ODo8OXl5eXo7efq7ujr7+rs7+zu8O3v8e3v8+/x8fDw8PD4//FtbfT09Pbx4//FxP/LUP/hcf/9nP///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////yH5BAEKAH8ALAAAAAAQABAAAAejgH+Cg4SFggyIiYqJEIclHkCRkkA4DxKNfwwkGJOROEAPB5gMJRSdODctOTkpggulCrGyChYhPCaOJSUkuyQkFyAaGxWOAsbHFz8+IAajJAMuLCw3Fso9HA+YCyQELCosyT49HQsRoyIEKyzV4hwFBQ6jHwUsJyjtLu8Nox4F1DsoOEAjUCDBqAwFdASYcGKFCx0ECCDANAKCxYsQmmE0xJFQIAA7"; break;
			case "file_edit.gif": b64string = "R0lGODlhFgAWAOd8AAA8X7P4r3HaT5wlAABbw5u5zc3StQA6qZ6lsCakEWGk1bjL59+mQv/ObBRGoP37+YLV/4bM9x9Xj5p0Z22TsOr01TmN14I2H4KUq//xtmeErZ5rKZjmsdHc7NL/0KCnhVpfeWuDpOJOPqXo/1a5b459bC1608z99KyzuXGnyzx+sqfH6//bm/jDY+v//9zZ2p/4/+Xk44eYq114o215iq/X9M4+IWM0K1CJxvf3937E8/Tx77aUhRqAQEKpTZXd/wB78eHHvf+uN6FaN6zE5pTR8bX09ypswLLu/3LIltTV1tv7/mO77V5wisGCIHC98f/phrLC0LauqJ3n2s/t+rHV3fbf2727uR1xmkvAKtLPz/b38lRni27VdLa7ytv/9sDe9Y8aAJKsuv/ASdiShKvr6jVmqO3u7xZ0zpvX9KGVk2SXwbz6//r8/VPFM875+8GBZf/mrABZvuHf3mqg1WqLu2Cv6Jfq/7y4t1e4/8fEwqh5RP///7X2//b0wMLw/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////yH5BAEKAIAALAAAAAAWABYAAAj+APkI5POgoMGCOXII3GLFyoOBAl/giaHlSgw9el5I0cJnCxwbIsicGdiBBg0vNEKgaCJDBhcEMQpcADmAh8IOdCysEYPDAgYTKuqYCFFHh5chA8JM2NFmwZM/URBQKYCCipgqYGSkoOJCB4gbUto01dFmjhI+czq06fDmT5U4cVbkAaIiBp82RCxQ4eOCSlskSH4UqdCghQEKErwIbLMCDRg+NYqkmTy5CosxLKA4sTmQCIEafKhQWUJ6yQk/Y1pkYLDHbmcCafi6WPKmzwgtY4TEabHBSxuInou0SVOkOAQIBoQ00KzGBUQ+wV2MGAG47YogUBiU6PAcOoEILt6ouPhywkMAFWY+0Kgz8rnnJ0s4CBDgJkuWHgCwmKjjGjiBJ190kYAPJJCQBAcpWHCEBnN0994bU0xRhhFGADbCE0fM0OBzCxDAxBsW/hBBBDro8IMCGrzQ3QJy2MEGiSNG8MOMMDChwVkcHmBHHzNOdwcETyiwxgwI7NBdBw6YwCOQKGqAAQJeKGFkd03VsQYFIciAghYxnLHDQ91BdMYLL5yxxRZhdhcQADs="; break;
			case "spellcheck.png": b64string = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJ0SURBVHjaYvz//z8DJQAggJgYKAQAAcQCIpz7bGdys3MZbMreae7QaxPPzcFZ/uHDp8OMjAyKR6pOuukXaJX8/f3X/Me3P+8Y/v1nuLPwXjrMAIAAYgR5wW+q+8nf/35//PbzZ8W/v/90gYqtjtWcSrdpt7j26dkHZzZejr1n2i5oYXMBQAAxOvfZxP/9/9/q37//t//9+asK1HyMhYO1/OfnH0++vv96//e3P8f+/mWwurPgbjqyRvGCGRyvXpn9BQggJhYW5uh///8LATWqcnFzeP76/pvh84uPh082nXP78/2f0J+f/17x8PLYotgad8H4lZDTBQYGpokAAcQCtJn/YPFRN5CESaX+zN/ff4uy8bDbqiQqz+Tk4pD7+eXXhc/vPm3UTNLY9effHykDGTsZcaManjdf/nz4++JHE0AAMRIbjYxR5yQY1Hj2u6tzaxy5+/Xr111v9P8fsroLEEAMIAPAhtTeTGAov+kK4yNjhtCzPuK9d/8Ernjyn7n2+jsGy8PSMDmAAGKBW/GD4ZSkHtc5xpobqxle/Un7P0vnO2PEOSYGFoaJlp6iOeLsTAzrz35+wXDgleH/U3YvYNoAAgjFC4xpV6p9vURatt358vzviU9pDAocte76vGacTEwMG06/f8xw4J3Z/wsIzSAAEECoBsRdYGFQ4DodZsBv8Pj9DwZ9UQ6GFx9+M2w48eE+w4E3Vv+vObxADxuAAMIIREavUwrCNkJ30oz5ma8+/86w6di7O0Cbbf/fcnqBLXABAogBa6CZHixymnb/P0Py+XsMirslsKmBYYAAwi2hubeDQRa/ZhAGCCBGSrMzQIABAIDZeasHWK/yAAAAAElFTkSuQmCC"; break;
			//case "": b64string = ""; break;
			
			default: System.err.println("Base64 data for image '" + textId + "' not found.");
		}
		
		try
		{
			byte[] decodedBytes = Base64.getDecoder().decode(b64string); 
			BufferedImage source = ImageIO.read(new ByteArrayInputStream(decodedBytes));
			Image image = source.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			return new ImageIcon(image);
		} catch (Exception e) {
			System.err.println("Base64 decoding failed and/or image couldn't be created: " + e.getMessage());
			return null;
		}
		
		/*
			yourImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
			https://stackoverflow.com/questions/5895829/resizing-image-in-java
		*/
	}
	
	public Editor(String[] args) {
		mainWindowReference = this;
		
		// Load configuration from file
		try {
			BufferedReader reader = new BufferedReader(new FileReader("config.txt"));
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				if (ligne.length() > 0)
				{
					String[] parts = ligne.split("\t");
					System.out.println(parts[0] + "=" + parts[1]);
					configs.put(parts[0], parts[1]);
				}				
			}
			reader.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("The configuration file contains errors.");
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			//System.exit(1);
		}

		// Apply the settings loaded from the configuration file
		try {
			tabSize = Integer.parseInt(configs.get("tab"));
		} catch (Exception e) {
			System.err.println("'tab' is missing from the configuration file or has an invalid value.");
		}
		try {
			defaultFont = configs.get("font");
		} catch (Exception e) {
			System.err.println("'font' is missing from the configuration file or has an invalid value.");
		}
		try {
			language = configs.get("lang");
		} catch (Exception e) {
			System.err.println("'lang' is missing from the configuration file or has an invalid value.");
		}
		try {
			textSize = Integer.parseInt(configs.get("zoom"));
		} catch (Exception e) {
			System.err.println("'zoom' is missing from the configuration file or has an invalid value.");
		}
		try {
			lineNumbering = Boolean.parseBoolean(configs.get("lines"));
		} catch (Exception e) {
			System.err.println("'lines' is missing from the configuration file or has an invalid value.");
		}
		try {
			lineWarp = Boolean.parseBoolean(configs.get("warp"));
		} catch (Exception e) {
			System.err.println("'warp' is missing from the configuration file or has an invalid value.");
		}
		try {
			statusBar = Boolean.parseBoolean(configs.get("stats"));
		} catch (Exception e) {
			System.err.println("'stats' is missing from the configuration file or has an invalid value.");
		}
		try {
			port = Integer.parseInt(configs.get("port"));
		} catch (Exception e) {
			System.err.println("'port' is missing from the configuration file or has an invalid value.");
		}
		try {
			lastIP = configs.get("lastip");
		} catch (Exception e) {
			System.err.println("'lastip' is missing from the configuration file or has an invalid value.");
		}
		try {
			lastCommand = configs.get("lastcmd");
		} catch (Exception e) {
			System.err.println("'lastcmd' is missing from the configuration file or has an invalid value.");
		}
		
		// Layout
		JPanel mainFrame = new JPanel();
		mainFrame.setLayout(new FlowLayout());
		//this.setLayout(new SpringLayout());
		//this.add(this, BorderLayout.CENTER);
	
		// Set what to display, do a label in the main area
		JLabel lengthLabel = new JLabel("label");
		JLabel lineLabel = new JLabel("label2");
		lengthLabel.setPreferredSize(new Dimension(250, 20));
		lineLabel.setPreferredSize(new Dimension(100, 20));
		//frame.setDefaultLookAndFeelDecorated(true);
		
		// Add a textarea
		JTextArea txtArea = new JTextArea();
		//txtArea.setLocation(0, 0);
		//txtArea.setPreferredSize(new Dimension(640, 480));
		txtArea.setTabSize(tabSize);
		txtArea.setFont(new Font(defaultFont, Font.PLAIN, textSize));
		// textArea.setLineWrap( true );
		// textArea.setWrapStyleWord( true );
		undoList.push(txtArea.getText());

		// Add to the frame, put the textarea in a scrollpane first so we get the scrollbars
		JScrollPane scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED/*, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS*/);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		mainFrame.add(scrollPane);
		mainFrame.add(lengthLabel);
		mainFrame.add(lineLabel);
		
		// Add the layout to the frame
		this.add(mainFrame);
		
		// Set the window icon
		this.setIconImage(GetImageIcon("file_edit.gif").getImage());
	
		// Create the window
		//JFrame frame = new JFrame("FrameDemo");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 300));
		this.setTitle("TeamPad - Simple Cooperative Text Editor");
		this.setSize(400, 300);
				
		// Add a menu bar to the frame
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		// File menu
		JMenu fileMenu = new JMenu(GetStringForLang("File"));
		menuBar.add(fileMenu);
		// Create items of the file menu
		JMenuItem newAction = new JMenuItem(GetStringForLang("New"), GetImageIcon("Document New-01.png"));
		JMenuItem openAction = new JMenuItem(GetStringForLang("Open"), GetImageIcon("Open_file.png"));
		JMenuItem saveAction = new JMenuItem(GetStringForLang("Save"), GetImageIcon("save.png"));
		JMenuItem saveAsAction = new JMenuItem(GetStringForLang("Save As"), GetImageIcon("saveas_icon.gif"));
		JMenuItem exitAction = new JMenuItem(GetStringForLang("Exit"), GetImageIcon("exit-24-000000.png"));
		// Add the items to the file menu
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();		// Separator
		// The launcher for external programs: 
		JMenuItem runAction = new JMenuItem(GetStringForLang("Run"), GetImageIcon("debug.png"));
		fileMenu.add(runAction);
		fileMenu.addSeparator();	// Separator
		fileMenu.add(exitAction);
		
		exitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				System.exit(0);
			}
		});
		
		openAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				JFileChooser fileChooser = new JFileChooser();
				
				if (lastSelectedDirectory != "") {
					fileChooser.setCurrentDirectory(new File(lastSelectedDirectory));
				}
				
				int returnVal = fileChooser.showOpenDialog(mainWindowReference);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						System.out.println("Opening file: " + fileChooser.getSelectedFile().getName());
						lastSelectedDirectory = fileChooser.getCurrentDirectory().toString();
						BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile().getAbsolutePath()));
						
						undoList = new Stack<String>();
						redoList = new Stack<String>();
						
						txtArea.setText("");
						txtArea.read(reader, null);
						reader.close();
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}
				}
			}
		});

		// Edit menu
		JMenu editMenu = new JMenu(GetStringForLang("Edit"));
		menuBar.add(editMenu);
		// Create the items of the edit menu
		JMenuItem undoAction = new JMenuItem(GetStringForLang("Undo"), GetImageIcon("back_undo.png"));
		JMenuItem redoAction = new JMenuItem(GetStringForLang("Redo"), GetImageIcon("botao-refazer.png"));
		JMenuItem cutAction = new JMenuItem(GetStringForLang("Cut"), GetImageIcon("mai1444425541180_lowres_en-us.png"));
		JMenuItem copyAction = new JMenuItem(GetStringForLang("Copy"), GetImageIcon("icon_copy_n.png"));
		JMenuItem pasteAction = new JMenuItem(GetStringForLang("Paste"), GetImageIcon("1398640998.png"));
		JMenuItem selectAllAction = new JMenuItem(GetStringForLang("Select All"), GetImageIcon("edit_select_all.png"));
		JMenuItem selectLineAction = new JMenuItem(GetStringForLang("Select Line"), GetImageIcon("editor_panel_tab_icon.gif"));
		JMenuItem findAction = new JMenuItem(GetStringForLang("Find and replace"), GetImageIcon("edit_find_replace.png"));
		// Add the items to the edit menu
		editMenu.add(undoAction);
		editMenu.add(redoAction);
		editMenu.addSeparator();	// Separator
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.addSeparator();	// Separator
		editMenu.add(selectAllAction);
		editMenu.add(selectLineAction);
		editMenu.add(findAction);
		
		undoAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (!undoList.empty()) {
					redoList.push(txtArea.getText());
					txtArea.setText(undoList.pop());
				}
			}
		});
		
		redoAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (!redoList.empty()) {
					undoList.push(txtArea.getText());
					txtArea.setText(redoList.pop());
				}
			}
		});
		
		// Option menu
		JMenu optionMenu = new JMenu(GetStringForLang("Option"));
		menuBar.add(optionMenu);
		// Create the items of the option menu
		JMenuItem increaseAction = new JMenuItem(GetStringForLang("Increase text size"), GetImageIcon("nav_zoomin.png"));
		// Add an action to zoom in
		increaseAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				textSize += textSizeIncrement;
				txtArea.setFont(new Font(txtArea.getFont().getName(), Font.PLAIN, textSize));
			}
		});
		
		JMenuItem decreaseAction = new JMenuItem(GetStringForLang("Decrease text size"), GetImageIcon("nav_zoomout.png"));
		// Add an action to zoom out
		decreaseAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				textSize -= textSizeIncrement;
				txtArea.setFont(new Font(txtArea.getFont().getName(), Font.PLAIN, textSize));
			}
		});

		JCheckBoxMenuItem wordWarpAction = new JCheckBoxMenuItem(GetStringForLang("Word warp"), GetImageIcon("WordWrap.png"));
		JCheckBoxMenuItem statusBarAction = new JCheckBoxMenuItem(GetStringForLang("Status bar"), GetImageIcon("ui-status-bar.png"));
		
		// Fonts
		JMenuItem fontsAction = new JMenuItem(GetStringForLang("Font"), GetImageIcon("truetype.gif"));

		fontsAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				String[] fontlist = (GraphicsEnvironment.getLocalGraphicsEnvironment()).getAvailableFontFamilyNames();
				JComboBox jcb = new JComboBox<String>(fontlist);
				
				jcb.setSelectedItem(txtArea.getFont().getName());
				
				System.out.println("\nCURRENT=" + txtArea.getFont().getName());
				
				String[] options = { "OK" };
				
				int n = (int)JOptionPane.showOptionDialog(
					mainWindowReference, 
					jcb, 
					"Font selection", 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.PLAIN_MESSAGE, 
					null,
					options, options[0]);
					
				if (n >= 0) {
					txtArea.setFont(new Font(jcb.getSelectedItem().toString(), Font.PLAIN, textSize));
					System.out.println("\nFONT=" + jcb.getSelectedItem());
				}
			}
		});	

		// Languages
		JMenu languagesMenu = new JMenu(GetStringForLang("Language"));
		languagesMenu.setIcon(GetImageIcon("bubble_icon.gif"));
		ButtonGroup languagesGroup = new ButtonGroup();
		String[] languages = {"Chinese", "English", "French", "German", "Japanese", "Spanish", "Russian"};
		
		for (int i = 0; i < languages.length; i++) {
			// Add the languages to the menu
			JRadioButtonMenuItem newLanguage = new JRadioButtonMenuItem(languages[i]);
			languagesGroup.add(newLanguage);
			languagesMenu.add(newLanguage);
			
			if (newLanguage.getText().equals(language)) {
				newLanguage.setSelected(true);
			}
			
			// Add an action on languages items
			newLanguage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg) {
					if (language != ((JRadioButtonMenuItem) arg.getSource()).getText()) {
						language = ((JRadioButtonMenuItem) arg.getSource()).getText();
						JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("RestartTranslate"), GetStringForLang("Notice"), JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		
		// Add the items to the option menu
		optionMenu.add(increaseAction);
		optionMenu.add(decreaseAction);
		optionMenu.addSeparator();		// Separator
		optionMenu.add(wordWarpAction);
		optionMenu.add(statusBarAction);
		optionMenu.addSeparator();		// Separator
		/*optionMenu.add(fontAction);*/
		optionMenu.add(fontsAction);
		/*optionMenu.add(languageAction);*/
		optionMenu.add(languagesMenu);
		
		// Team menu
		JMenu teamMenu = new JMenu(GetStringForLang("Team"));
		menuBar.add(teamMenu);
		// Create the items of the team menu
		JCheckBoxMenuItem allowConnectionsAction = new JCheckBoxMenuItem(GetStringForLang("Allow connections"), GetImageIcon("netico.gif"));
		JMenuItem connectToAction = new JMenuItem(GetStringForLang("Connect to"), GetImageIcon("network_icon.jpg"));
		JMenuItem sendMessageAction = new JMenuItem(GetStringForLang("Send a message"), GetImageIcon("c02228162.jpg"));
		JMenuItem disconnectAllAction = new JMenuItem(GetStringForLang("Disconnect all"), GetImageIcon("icon_disconnect_agent.bmp.png"));
		// Add the items to the team menu
		teamMenu.add(allowConnectionsAction);
		teamMenu.add(connectToAction);
		teamMenu.addSeparator();
		teamMenu.add(sendMessageAction);
		teamMenu.add(disconnectAllAction);

		allowConnectionsAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (sock == null) {
					boolean enabled = allowConnectionsAction.isSelected();
					System.out.println("Allow connections: " + enabled);
					
					if (enabled) {
						System.out.println("Integrated server: Feature not implemented.");
					} else {
						// Don't allow connections
						System.out.println("Don't allow connections");
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "You must disconnect from any client first. ", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		connectToAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {

				if (!allowConnectionsAction.isSelected()) {
					if (sock == null) {
						String ip = (String)JOptionPane.showInputDialog(
							mainWindowReference,
							GetStringForLang("EnterIP"),
							GetStringForLang("AskIP"),
							JOptionPane.PLAIN_MESSAGE,
							GetImageIcon("network_icon.jpg"),
							null,
							lastIP);

						System.out.println("IP=" + ip);
						
						// Try to connect (this connects to a server)
						if ((ip != null) && (ip.length() > 0)) {
							System.out.println("Connection to... " + ip);
							
							try {
								sock = new Socket(ip, port);	// Timeout?
								reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
								writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);	// autoFlush must be true, else it won't send anything. 
								// Connection successful!
								lastIP = ip;
							} catch (Exception e) {
								System.err.println(e.getMessage());
								//connections.clear();
							}
						}
					} else {
						JOptionPane.showMessageDialog(mainWindowReference, "You can only have one connection at once.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "The integrated server must be disabled. ", "Cannot connect", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		disconnectAllAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (reader == null && writer == null && sock == null) {
					JOptionPane.showMessageDialog(mainWindowReference, "There are no connection to disconnect from.", "Already disconnected", JOptionPane.WARNING_MESSAGE);
				} else {
					// Close any socket or stream
					try {
						if (reader != null) {
							reader.close();
							reader = null;
						}
						if (writer != null) {
							writer.close();
							writer = null;
						}
						if (sock != null) {
							sock.close();
							sock = null;
						}
					} catch (Exception e) {
						System.err.println("There was an error while closing sockets and streams.");
					}
				}
			}
		});
		
		sendMessageAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (sock != null) {
					String m = (String)JOptionPane.showInputDialog(
						mainWindowReference,
						"This will broadcast a message to every connected peer.",
						"Send a message",
						JOptionPane.PLAIN_MESSAGE,
						GetImageIcon("c02228162.jpg"),
						null,
						"");

					System.out.println("message: " + m);
					
					// Send the message if it's valid
					if ((m != null) && (m.length() > 0)) {
						try {
							if (writer != null && sock != null) {
								writer.println(m);
								System.out.println("message is sent... " + m);
							} else {
								System.err.println("Not connected to anyone.");
							}
						} catch (Exception e) {
							System.err.println("message couldn't be sent.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "You are not connected to anyone.", "Can't send a message", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		// Help menu
		JMenu helpMenu = new JMenu(GetStringForLang("Help"));
		menuBar.add(helpMenu);
		// Create the menu items
		JMenuItem aboutAction = new JMenuItem(GetStringForLang("About"), GetImageIcon("iconInfo.gif"));
		// Add the help to the option menu
		helpMenu.add(aboutAction);
		
		// Display the window
		this.pack();
		this.setVisible(true);
		
		// Add an action
		aboutAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				//JOptionPane.showMessageDialog(null, "Written by: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nDistributed under the GNU GPL version 3", "About", JOptionPane.INFORMATION_MESSAGE);
				JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("Copying"), GetStringForLang("About"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		runAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				String p = (String)JOptionPane.showInputDialog(
					mainWindowReference,
					GetStringForLang("EnterPath"),
					GetStringForLang("CommandLine"),
					JOptionPane.INFORMATION_MESSAGE,
					GetImageIcon("debug.png"),
					null,
					lastCommand);

				System.out.println("PATH=" + p);
				
				// Start the application
				if ((p != null) && (p.length() > 0)) {
					try {
						Runtime rt = Runtime.getRuntime();
						Process pr = rt.exec(p);	// Execute
						lastCommand = p;
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}
				}
			}
		});
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent comp) {
				// the component was resized...
				System.out.println("window resized: (" + mainWindowReference.getWidth() + "," + mainWindowReference.getHeight() + ")");
				scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
			}
		});
		this.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent ev) {
				System.out.println("window state changed: " + ev);
				scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
			}
		});
		/*
		txtArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lengthLabel.setText("label " + txtArea.getText());
			}
		});
		*/
		txtArea.getDocument().addDocumentListener(new DocumentListener() {
			// This counts the file size, not the characters. 
			// If we want to count the characters, we need to remove the special ones (Unicode and control characters).
			public void changedUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
			public void insertUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
			public void removeUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
		});
		
		txtArea.addKeyListener(new KeyListener() { 
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					//e.consume();
					undoList.push(txtArea.getText());
					redoList = new Stack<String>();
				}
				
				// System info
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F12){
					JOptionPane.showMessageDialog(mainWindowReference, 
					"Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n" +
					System.getProperty("os.name") + " (" + System.getProperty("os.version") + ") " + 
					System.getProperty("os.arch") + " " + Runtime.getRuntime().availableProcessors() + "-cores", 
					GetStringForLang("SystemInfo"),
					JOptionPane.INFORMATION_MESSAGE);
				}
			}
			public void keyReleased(KeyEvent e) {
				// Do nothing, Java wants it to be overriden, but we don't want to change its default behavior. 
			}
			public void keyTyped(KeyEvent e) {
				// Do nothing, Java wants it to be overriden, but we don't want to change its default behavior. 
				System.out.print(".");
			}
		});

		txtArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				try {
					lineLabel.setText(GetStringForLang("Line") + " " + (txtArea.getLineOfOffset(txtArea.getCaretPosition()) + 1));
				} catch (BadLocationException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		
		// Size the window correctly
		this.setSize(400, 300);
		scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
		System.out.println(System.getProperty("os.name") + " (" + System.getProperty("os.version") + ") " + System.getProperty("os.arch") + " " + Runtime.getRuntime().availableProcessors() + "-cores");
		System.out.println("TEAMPAD VERSION 0.1");
		
		// Test: remove it
		try
		{
			Socket clientSocket = null; 
			
			ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(clientSocket.getOutputStream())); 
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(clientSocket.getInputStream())); 

			TextUpdate tu = (TextUpdate) in.readObject();
			out.writeObject(tu);
			out.flush();
			out.close();
			in.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		if (CheckParam(args, "-connect") >= 0) {
			System.out.println("Connect to ip parameter detected");
		}
		
		if (CheckParam(args, "-host") >= 0) {
			System.out.println("Create server parameter detected");
		}
		
		if (CheckParam(args, "-l") >= 0) {
			System.out.println("Goto line parameter detected");
		}
		
		if (CheckParam(args, "-f") >= 0) {
			System.out.println("Load file parameter detected");
		}
		
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		Editor window = new Editor(args);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	public static int CheckParam(String[] args, String arg) {
		return Arrays.asList(args).indexOf(arg);
	}
}
