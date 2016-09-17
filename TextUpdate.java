// Serialized class that can be sent across a TCP socket

import java.io.*;

class TextUpdate implements Serializable
{
	private String text;
	private int start;
	private int end;

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
	}

	public String toString()
	{
		return "(" + start + ") " + text + " (" + end + ")";
	}
}