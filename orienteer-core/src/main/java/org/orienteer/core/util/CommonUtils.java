package org.orienteer.core.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Session;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Common for Orienteer utility methods
 */
public class CommonUtils {
	
	private CommonUtils() {
		
	}

	/**
	 * Converts given objects into map.
	 * Uses pairs of object.
	 * Call toMap("key1", "value1", "key2", "value2") will returns this map:
	 * { "key1": "value1", "key2": "value2" }
	 * Call method with not pair arguments will throw {@link IllegalStateException}.
	 * For example: toMap("key1", "value1", "key2") - throws {@link IllegalStateException}
	 * @param objects {@link Object[]} array of objects which will be used for create new map
	 * @param <K> type of map key
	 * @param <V> type of map value
	 * @return {@link Map} created from objects
	 * @throws IllegalStateException if objects are not pair
	 */
	public static final <K, V> Map<K, V> toMap(Object... objects) {
		if(objects==null || objects.length % 2 !=0) throw new IllegalArgumentException("Illegal arguments provided to construct a map");
		Map<K, V> ret = new HashMap<K, V>();
		for(int i=0; i<objects.length; i+=2) {
			ret.put((K)objects[i], (V)objects[i+1]);
		}
		return ret;
	}
	
	public static final Object localizeByMap(Map<String, ?> map, boolean returnFirstIfNoMatch, String... languages) {
		if(map==null) return null;
		for(int i=0; i<languages.length;i++) {
			if(map.containsKey(languages[i])) return map.get(languages[i]);
		}
		if(returnFirstIfNoMatch && !map.isEmpty()) return map.values().iterator().next();
		else return null;
	}

	/**
	 * Converts value to string. See {@link CommonUtils#objectToString(Object)}
	 * @param value {@link String} value which need convert to string
	 * @return {@link String} converted value to string or empty string if can't convert value to string or value is null
	 */
	public static final String objectToString(Object value) {
		return objectToString(value, "");
	}

	/**
	 * Convert given object to localized string
	 * Uses Wicket {@link IConverter}
	 * @param value {@link Object} value which need convert to string
	 * @param defaultValue {@link String} default value if can't convert value to string or value is null
	 * @return {@link String} value converted into string
	 */
	public static final String objectToString(Object value, String defaultValue) {
		String ret = null;
		if(value!=null) {
			final Class<?> objectClass = value.getClass();
			final IConverter converter = OrienteerWebApplication.get().getConverterLocator().getConverter(objectClass);
			ret = converter.convertToString(value, OrienteerWebSession.get().getLocale());
		}
		return ret!=null?ret:defaultValue;
	}

	/**
	 * Convert content to JavaScript string.
	 * Added '"' at start and end of content
	 * Replaces all '\n' by '\\n"'
	 * @param content {@link CharSequence} content
	 * @return {@link CharSequence} JavaScript string or "null" if content is null
	 */
	public static final CharSequence escapeAndWrapAsJavaScriptString(CharSequence content) {
		if(content==null) return "null";
		else {
			content = JavaScriptUtils.escapeQuotes(content);
			content = "\"" + content + "\""; 
			content = Strings.replaceAll(content, "\r", "");
			content = Strings.replaceAll(content, "\n", "\" + \n\"");
			return content;
		}
	}

	/**
	 * Convert content to JSON string.
	 * Replace all '\"' by '\\\"'.
	 * Replace all '\n' by '\\n\"'
	 * @param content {@link CharSequence} content
	 * @return {@link CharSequence} JSON string or empty string "" if content is null
	 */
	public static final CharSequence escapeStringForJSON(CharSequence content) {
		if(content==null) return "";
		else {
			content = Strings.replaceAll(content, "\r", "");
			content = Strings.replaceAll(content, "\"", "\\\"");
			content = Strings.replaceAll(content, "\n", "\" + \n\"");
			return content;
		}
	}

	/**
	 * Map given list of {@link OIdentifiable} uses given mapping function mapFunc
	 * Before apply mapFunc loads record and cast it to {@link ODocument} from identifiable, by calling {@link OIdentifiable#getRecord()}
	 * If record is null, so discard this identifiable and doesn't apply mapFunc to it.
	 * @param identifiables {@link OIdentifiable} list of {@link OIdentifiable} for map to value
	 * @param mapFunc  map function
	 * @param <T> - type of return value in map function
	 * @return mapped list of {@link OIdentifiable} or empty list if identifiables is null
	 */
	public static <T> List<T> mapIdentifiables(List<OIdentifiable> identifiables, Function<ODocument, T> mapFunc) {
		return mapIdentifiables(identifiables, mapFunc, null);
	}
	
	/**
	 * Map given list of {@link OIdentifiable} uses given mapping function mapFunc and apply filter
	 * Before apply mapFunc loads record and cast it to {@link ODocument} from identifiable, by calling {@link OIdentifiable#getRecord()}
	 * If record is null, so discard this identifiable and doesn't apply mapFunc to it.
	 * @param identifiables {@link OIdentifiable} list of {@link OIdentifiable} for map to value
	 * @param mapFunc  map function
	 * @param filter filter to be applied
	 * @param <T> - type of return value in map function
	 * @return mapped list of {@link OIdentifiable} or empty list if identifiables is null
	 */
	public static <T> List<T> mapIdentifiables(List<OIdentifiable> identifiables, Function<ODocument, T> mapFunc, Predicate<? super T> filter) {
		if (identifiables == null) {
			return Collections.emptyList();
		}
		Stream<T> stream  = identifiables.stream()
											.map(i -> (ODocument) i.getRecord())
											.filter(Objects::nonNull)
											.map(mapFunc);
		if(filter!=null) stream = stream.filter(filter);
		return stream.collect(Collectors.toList());
	}

	/**
	 * Get list of documents from list of identifiables.
	 * Uses {@link CommonUtils#mapIdentifiables(List, Function)}
	 * @param identifiables list of identifiables
	 * @return list of documents
	 */
	public static List<ODocument> getDocuments(List<OIdentifiable> identifiables) {
		return mapIdentifiables(identifiables, d -> d);
	}

	/**
	 * Get first item in identifiables and apply mapFunc
	 * First {@link OIdentifiable} from identifiables load record and cast it to {@link ODocument}.
	 * If record is null, so mapFunc doesn't apply to it and returns null.
	 * @param identifiables list of identifiables
	 * @param mapFunc map function which will by apply for record from first item in identifiables
	 * @param <T> type of return value by mapFunc
	 * @return optional mapped record from first item in identifiables or {@link Optional#empty()}
	 * if identifiable is empty or can't load record from first identifiable
	 */
	public static <T> Optional<T> getFromIdentifiables(List<OIdentifiable> identifiables, Function<ODocument, T> mapFunc) {
		return isNotEmpty(identifiables) ? getFromIdentifiable(identifiables.get(0), mapFunc) : empty();
	}

	/**
	 * Map record from identifiable, using map function
	 * If can't load record and cast it to {@link ODocument} from identifiable, so returns null
	 * @param identifiable {@link OIdentifiable} identifiable for map
	 * @param mapFunc map function which will be apply for record loaded from identifiable
	 * @param <T> type of return value by mapFunc
	 * @return optional mapped record or {@link Optional#empty()} if identifiable is null, or can't load record
	 */
	public static <T> Optional<T> getFromIdentifiable(OIdentifiable identifiable, Function<ODocument, T> mapFunc) {
		if (identifiable != null) {
			ODocument doc = identifiable.getRecord();
			return doc != null ? ofNullable(mapFunc.apply(doc)) : empty();
		}
		return empty();
	}

	/**
	 * Check if given collection is not empty
	 * @param collection collection to test
	 * @param <T> type of collection
	 * @return true if collection is not empty
	 */
	public static <T> boolean isNotEmpty(Collection<T> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * Load record and cast it to {@link ODocument} from first ite in identifiables
	 * @param identifiables {@link List} identifiables
	 * @return get first document or {@link Optional#empty()}
	 */
	public static Optional<ODocument> getDocument(List<OIdentifiable> identifiables) {
		return isNotEmpty(identifiables) ? ofNullable(identifiables.get(0).getRecord()) : empty();
	}
	
	/**
	 * Return main object if it's not null or default
	 * @param <T> required return type
	 * @param object main object
	 * @param def default object
	 * @return main object if it's not null or default
	 */
	public static <T> T defaultIfNull(T object, T def) {
		return object!=null?object:def;
	}
	
	/**
	 * Return main object if it's not null or supplied default
	 * @param <T> required return type
	 * @param object main object
	 * @param supplier supplier of default object
	 * @return main object if it's not null or supplied default
	 */
	public static <T> T defaultIfNull(T object, Supplier<T> supplier) {
		return object!=null?object:supplier.get();
	}
	
	/**
	 * Combine array of {@link Optional} and return first not empty
	 * @param <T> type of required {@link Optional} 
	 * @param optionals array of {@link Optional}s
	 * @return first not empty {@link Optional}
	 */
	public static <T> Optional<T> orOptional(Optional<T>... optionals) {
		for (Optional<T> optional : optionals) {
			if(optional.isPresent()) return optional;
		}
		return Optional.empty();
	}
	
	/**
	 * Safe method to merge sets. Always return not null
	 * @param <T> type of sets and required result
	 * @param mainSet main set to merge into
	 * @param mergeSet set to merge
	 * @return mergedSet - not null
	 */
	public static <T> Set<T> mergeSets(Set<T> mainSet, Collection<T> mergeSet) {
		if(mainSet==null) mainSet = new HashSet<>();
		if(mergeSet!=null) mainSet.addAll(mergeSet);
		return mainSet;
	}
	
	/**
	 * Safe method to merge maps
	 * @param <K> type of keys in map
	 * @param <V> type of values in map
	 * @param mainMap main map to merge into
	 * @param mergeMap map to merge
	 * @return merged map - not null
	 */
	public static <K, V> Map<K,V> mergeMaps(Map<K, V> mainMap, Map<K,V> mergeMap) {
		if(mainMap==null) mainMap = new HashMap<>();
		if(mergeMap!=null) mainMap.putAll(mergeMap);
		return mainMap;
	}
	
	/**
	 * Safe method to check that 2 {@link OIdentifiable}s are actually the same
	 * @param a first {@link OIdentifiable}
	 * @param b second {@link OIdentifiable}
	 * @return true if 2 objects are referring to the same document
	 */
	public static boolean isTheSame(OIdentifiable a, OIdentifiable b) {
		if(a==null && b==null) return true;
		else if((a==null && b!=null) || (a!=null && b==null)) return false;
		else return a.getIdentity().equals(b.getIdentity());
	}
	
	/**
	 * Safe method to get String representation of an object.
	 * Wicket convertions are also has been used
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toString(Object data) {
		if(data==null) return "null";
		else if (data instanceof CharSequence) return data.toString();
		else {
			IConverter<Object> converter = (IConverter<Object>)OrienteerWebApplication.lookupApplication()
																	.getConverterLocator().getConverter(data.getClass());
			if(converter!=null) {
				return converter.convertToString(data, Session.exists()?Session.get().getLocale():Locale.getDefault());
			} else {
				return data.toString();
			}
		}
	}
}
