package com.streamerredeems;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StreamerRedeemsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(StreamerRedeemsPlugin.class);
		RuneLite.main(args);
	}
}