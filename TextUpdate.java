// Copyright (C) 2015-2017  Alexandre-Xavier Labonté-Lamoureux

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

// Serializable class that can be sent across a TCP socket as an object

import java.io.Serializable;

class TextUpdate implements Serializable {

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
		return time%10000 + ":$" + start + "$" + text + "$" + end + "$";
	}
}
