package ru.ydn.orienteer.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.inject.Singleton;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.lang.Objects;

import ru.ydn.orienteer.services.IUmlService;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

@Singleton
public class PlantUmlService implements IUmlService
{
	@Inject(optional=true)
	@Named("plantuml.url")
	private String urlPrefix = "http://www.plantuml.com/plantuml/png/";
	
	@Inject(optional=true)
	@Named("plantuml.showuml")
	private boolean showUml = false;
	
	private static class AsciiEncoder{
		
		static AsciiEncoder INSTANCE = new AsciiEncoder();

		final private char encode6bit[] = new char[64];

		public AsciiEncoder() {
			for (byte b = 0; b < 64; b++) {
				encode6bit[b] = encode6bit(b);
			}
		}

		public String encode(byte data[]) {
			final StringBuilder resu = new StringBuilder((data.length * 4 + 2) / 3);
			for (int i = 0; i < data.length; i += 3) {
				append3bytes(resu, data[i] & 0xFF, i + 1 < data.length ? data[i + 1] & 0xFF : 0,
						i + 2 < data.length ? data[i + 2] & 0xFF : 0);
			}
			return resu.toString();
		}

		public char encode6bit(byte b) {
			assert b >= 0 && b < 64;
			if (b < 10) {
				return (char) ('0' + b);
			}
			b -= 10;
			if (b < 26) {
				return (char) ('A' + b);
			}
			b -= 26;
			if (b < 26) {
				return (char) ('a' + b);
			}
			b -= 26;
			if (b == 0) {
				return '-';
			}
			if (b == 1) {
				return '_';
			}
			assert false;
			return '?';
		}

		private void append3bytes(StringBuilder sb, int b1, int b2, int b3) {
			final int c1 = b1 >> 2;
			final int c2 = ((b1 & 0x3) << 4) | (b2 >> 4);
			final int c3 = ((b2 & 0xF) << 2) | (b3 >> 6);
			final int c4 = b3 & 0x3F;
			sb.append(encode6bit[c1 & 0x3F]);
			sb.append(encode6bit[c2 & 0x3F]);
			sb.append(encode6bit[c3 & 0x3F]);
			sb.append(encode6bit[c4 & 0x3F]);
		}

	}

	@Override
	public String describe(OSchema schema)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("@startuml");
		pw.println();
		describe(pw, schema);
		pw.println("@enduml");
		pw.close();
		return sw.toString();
	}
	
	@Override
	public String describe(OClass oClass)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("@startuml");
		pw.println();
		describe(pw, oClass);
		pw.println("@enduml");
		pw.close();
		return sw.toString();
	}
	
	@Override
	public String describe(boolean goUp, boolean goDown, OClass... oClasses) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("@startuml");
		pw.println();
		describe(pw, goUp, goDown, oClasses);
		pw.println("@enduml");
		pw.close();
		return sw.toString();
	}

	@Override
	public String describe(OProperty oProperty)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("@startuml");
		pw.println();
		describe(pw, oProperty);
		pw.println("@enduml");
		pw.close();
		return sw.toString();
	}
	
	public void describe(Writer writer, OSchema schema)
	{
		PrintWriter out = toPrintWriter(writer);
		
		for(OClass oClass : schema.getClasses())
		{
			describe(out, oClass);
		}
	}
	
	public void describe(Writer writer, OClass oClass)
	{
		PrintWriter out = toPrintWriter(writer);
		out.append(oClass.isAbstract()?"abstract":"class").append(" ").append(oClass.getName());
		if(oClass.getSuperClass()!=null) out.append(" extends ").append(oClass.getSuperClass().getName());
		out.println();
		for(OProperty property: oClass.declaredProperties())
		{
			describe(out, property);
		}
		out.println();
	}
	
	public void describe(Writer writer, boolean goUp, boolean goDown, OClass... oClasses)
	{
		PrintWriter out = toPrintWriter(writer);
		Collection<OClass> allClasses=null;
		if(goUp || goDown)
		{
			allClasses = new HashSet<OClass>(oClasses.length*2);
			Set<OClass> inClasses = new HashSet<OClass>(Arrays.asList(oClasses));
			for(OClass oClass: inClasses)
			{
				allClasses.add(oClass);
				if(goUp)
				{
					OClass parent;
					while((parent = oClass.getSuperClass())!=null)
					{
						allClasses.add(parent);
					}
				}
				if(goDown)
				{
					allClasses.addAll(oClass.getAllBaseClasses());
				}
			}
		}
		else
		{
			allClasses = Arrays.asList(oClasses);
		}
		
		for(OClass oClass : allClasses)
		{
			describe(out, oClass);
		}
	}
	
	public void describe(Writer writer, OProperty oProperty)
	{
		PrintWriter out = toPrintWriter(writer);
		OType type = oProperty.getType();
		String min = oProperty.getMin();
		String max = oProperty.getMax();
		String range = null;
		
		if(min!=null || max!=null)
		{
			range = Objects.equal(min, max)?min:(min!=null?min:"0")+".."+(max!=null?max:"*");
		}
		else if(type.isMultiValue())
		{
			range = "*";
		}
		
		boolean isEmbedded = type.equals(OType.EMBEDDED) || type.equals(OType.EMBEDDEDLIST) 
				|| type.equals(OType.EMBEDDEDMAP) || type.equals(OType.EMBEDDEDSET);
		
		if(oProperty.getLinkedClass()!=null
				&& (isEmbedded || type.isLink()))
		{
			out.append(oProperty.getOwnerClass().getName());
			if(isEmbedded) out.append("\"1\" *-- ");
			else out.append(" -> ");
			if(range!=null) out.append('"').append(range).append("\" ");
			out.append(oProperty.getLinkedClass().getName());
			out.append(" : ").append(oProperty.getName());
		}
		else
		{
			out.append(oProperty.getOwnerClass().getName())
				.append(" : ")
				.append(oProperty.getName()).append(" : ").append(type.name());
		}
		out.println();
	}
	
	private PrintWriter toPrintWriter(Writer writer)
	{
		return writer instanceof PrintWriter?(PrintWriter)writer:new PrintWriter(writer);
	}
	
	public String asImage(String content)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater(9, true));
		try
		{
			dos.write(content.getBytes());
			dos.flush();
			dos.close();
			//return urlPrefix+URLEncoder.encode(Base64.encodeBase64String(baos.toByteArray()), "UTF-8");
			return urlPrefix+AsciiEncoder.INSTANCE.encode(baos.toByteArray());
		} catch (IOException e)
		{
			throw new WicketRuntimeException("Can't encrypt content for '"+PlantUmlService.class.getSimpleName()+"'", e);
		}
	}

	@Override
	public String describeAsImage(OSchema schema) {
		return asImage(describe(schema));
	}

	@Override
	public String describeAsImage(OClass oClass) {
		return asImage(describe(oClass));
	}

	@Override
	public String describeAsImage(boolean goUp, boolean goDown, OClass... oClass) {
		return asImage(describe(goUp, goDown, oClass));
	}

	@Override
	public String describeAsImage(OProperty oProperty) {
		return asImage(describe(oProperty));
	}

	@Override
	public boolean isUmlDebugEnabled() {
		return showUml;
	}
	
}
