package messaging;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import utilities.Utils;

public class Event implements Serializable {

	private static final long serialVersionUID = 4986172999588690076L;
	private String topic;
	private Object[] arguments = null;
	
	public Event() {};
	public Event(String topic, Object... arguments) {
		this.topic = topic;
		this.arguments = arguments;
	}
	
	public Event(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}
	
	private Object[] getArguments() {
		return arguments;
	}
	
	public <T> T getArgument(int i, Class<T> cls) {
		// The hack is needed because of Events are converted
		// to JSon for transport. Because JSon does not store
		// the class of an Object, when deserializing the arguments
		// of an Event, LinkedTreeLists are returned, which cannot be 
		// cast to real objects or converted to JSonObjects.
		// The trick is to generated a JSon string from the argument and 
		// then parse that string back to the class one needs.
		// This also works, for tests, where the arguments to an Event contain
		// the original objects.
		var gson = new Gson();
		var jsonString = gson.toJson(arguments[i]);
		return gson.fromJson(jsonString, cls);
	}
	
	public <T> T getArgument(int i, Type cls) {
		// See the comment from the other getArgument method.
		// In addition, this allows to get list of objects
		// Here the parameters needs to be
		// new TypeToken<List<YourClass>>(){}.getType();
		var gson = new Gson();
		var jsonString = gson.toJson(arguments[i]);
		return gson.fromJson(jsonString, cls);
	}

	public boolean equals(Object o) {
		if (!this.getClass().equals(o.getClass())) {
			return false;
		}
		Event other = (Event) o;
		return this.topic.equals(other.topic) &&
				(this.getArguments() != null &&
				Arrays.equals(getArguments(),other.getArguments())) ||
				(this.getArguments() == null && other.getArguments() == null);
	}
	
	public int hashCode() {
		return topic.hashCode();
	}
	
	public String toString() {
		List<String> strs = new ArrayList<>();
		if (arguments != null) {
			List<Object> objs = Arrays.asList(arguments);
			strs = objs.stream().map(o -> o.toString()).collect(Collectors.toList());
		}
		
		return String.format("event(%s,%s)", topic,String.join(",", strs));
	}
	
	public void logSend() {
		Utils.logSend(this);
	}
	public void logHandle() {
		Utils.logHandle(this);
	}
}
