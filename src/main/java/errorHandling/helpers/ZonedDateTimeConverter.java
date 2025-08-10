package errorHandling.helpers;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeConverter
	extends JsonSerializer<ZonedDateTime>
{

	@Override
	public void serialize(ZonedDateTime value, JsonGenerator jgen, SerializerProvider serializers)
		throws IOException
	{
		if (null != value)
		{
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss z (XXX)");
            String zonedDateTimeAsString = value.format(formatter1);
            
			jgen.writeString(zonedDateTimeAsString);
		}
	}

}
