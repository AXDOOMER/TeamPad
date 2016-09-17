// Serialized class that can be sent across a TCP socket

import java.io.*;
import java.util.Date;

class TextUpdate implements Serializable
{
	private String text;
	private int start;
	private int end;
	private long time;		// For debug

	public String text()
	{
		return text;
	}

	public int start()
	{
		return start;
	}

	public int end()
	{
		return end;
	}

	public TextUpdate(String t, int s, int e)
	{
		text = t;
		start = s;
		end = e;
		time = System.currentTimeMillis();
	}

	public String toString()
	{
		return time%1000 + ": (" + start + ") " + text + " (" + end + ")";
	}
}