package ru.ydn.orienteer.components;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.util.io.IClusterable;

public interface IMetaComponentResolver<C> extends IClusterable{
	public Component resolve(String id, C critery);
	public Serializable getSignature(C critery);
}
