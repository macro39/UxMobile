package sk.uxtweak.uxmobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import sk.uxtweak.uxmobile.model.ViewEnum;
import sk.uxtweak.uxmobile.model.event.ClickEvent;
import sk.uxtweak.uxmobile.model.event.CustomEvent;
import sk.uxtweak.uxmobile.model.event.FlingEvent;
import sk.uxtweak.uxmobile.model.event.LongPressEvent;
import sk.uxtweak.uxmobile.model.event.OrientationEvent;
import sk.uxtweak.uxmobile.model.event.ScrollEvent;
import sk.uxtweak.uxmobile.model.study.Task;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JsonConversionUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void clickEventConversion_isCorrect() throws Exception {

        final String type = "c";
        final long startTime = 0;
        final double x = 1;
        final double y = 1;
        final ViewEnum viewEnum = ViewEnum.BUTTON;
        final int viewEnumValue = 4; // ViewEnum.BUTTON.getJsonValue()
        final String viewText = "hello";
        final String viewInfo = "basic_info";

        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                x + "," +
                y + "," +
                viewEnumValue + "," +
                "\"" + viewText + "\"" + "," +
                "\"" + viewInfo + "\"" + "," +
                "]");

        ClickEvent clickEvent = new ClickEvent(startTime, x, y, viewEnum, viewText, viewInfo);

        assertEquals(expected.toString(), clickEvent.toJson().toString());
    }

    @Test
    public void longPressEventConversion_isCorrect() throws Exception {

        final String type = "l";
        final long startTime = 0;
        final double x = 1;
        final double y = 1;
        final ViewEnum viewEnum = ViewEnum.BUTTON;
        final int viewEnumValue = 4; // ViewEnum.BUTTON.getJsonValue()
        final String viewText = "hello";
        final String viewInfo = "basic_info";

        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                x + "," +
                y + "," +
                viewEnumValue + "," +
                "\"" + viewText + "\"" + "," +
                "\"" + viewInfo + "\"" + "," +
                "]");

        LongPressEvent longPressEvent = new LongPressEvent(startTime, x, y, viewEnum, viewText, viewInfo);

        assertEquals(expected.toString(), longPressEvent.toJson().toString());
    }

    @Test
    public void customEventConversion_isCorrect() throws Exception {

        final String type = "u";
        final long startTime = 0;
        final String customEventName = "customName";
        final String key1 = "test1";
        final String value1 = "test_value1";
        final String key2 = "test2";
        final String value2 = "test_value2";


        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                "'" + customEventName + "'" + "," +
                "{" +
                "\"" + key1 + "\": \"" + value1 + "\"," +
                "\"" + key2 + "\": \"" + value2 + "\"," +
                "}]");

        Map<String, String> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);

        CustomEvent customEvent = new CustomEvent(startTime, customEventName, map);

        assertEquals(expected.toString(), customEvent.toJson().toString());
    }

    @Test
    public void scrollEventConversion_isCorrect() throws Exception {

        final String type = "s";
        final long startTime = 0;
        final double x = 1;
        final double y = 1;
        final double distanceX = 1;
        final double distanceY = 1;

        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                x + "," +
                y + "," +
                distanceX + "," +
                distanceY +
                "]");

        ScrollEvent scrollEvent = new ScrollEvent(startTime, x, y, distanceX, distanceY);

        assertEquals(expected.toString(), scrollEvent.toJson().toString());
    }

    @Test
    public void flingEventConversion_isCorrect() throws Exception {

        final String type = "f";
        final long startTime = 0;
        final double x = 1;
        final double y = 1;
        final double velocityX = 1;
        final double velocityY = 1;

        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                x + "," +
                y + "," +
                velocityX + "," +
                velocityY +
                "]");

        FlingEvent flingEvent = new FlingEvent(startTime, x, y, velocityX, velocityY);

        assertEquals(expected.toString(), flingEvent.toJson().toString());
    }

    @Test
    public void orientationEventConversion_isCorrect() throws Exception {

        final String type = "o";
        final long startTime = 0;
        final int orientation = 1;

        final JSONArray expected = new JSONArray("[" +
                type + "," +
                startTime + "," +
                orientation +
                "]");

        OrientationEvent orientationEvent = new OrientationEvent(startTime, orientation);

        assertEquals(expected.toString(), orientationEvent.toJson().toString());
    }

    @Test
    public void taskConversion_isCorrect() throws Exception {

        final long id = 123;
        final String title = "test_title";
        final String message = "test_message";

        final Task expected = new Task(id, title, message);

        final JSONObject jsonObject = new JSONObject("{" +
                "\"task_id\" : " + id + ",\n" +
                "\"title\" : " + title + ",\n" +
                "\"message\": " + message + "\n" +
                "}");

        assertEquals(expected, Task.fromJson(jsonObject));
    }

    @Test
    public void eventRecordingConversion_isCorrect() throws Exception {
        final int startTime = 0;
        final String activityName = "activities.DiaryMainActivity";

        final EventRecording eventRecording = new EventRecording(activityName, startTime);
        eventRecording.addEvent(new ScrollEvent(2058, 0.5417786836624146, 0.7651091814041138, -0.0015757083892822266, 0.013917083851993084));
        eventRecording.addEvent(new ScrollEvent(2207, 0.5841091871261597, 0.5248535871505737, -0.006710481829941273, 0.028040390461683273));
        eventRecording.addEvent(new OrientationEvent(8350, 1));
        eventRecording.addEvent(new FlingEvent(9929, 0.7540574669837952, 0.7047873735427856, 0.3126640021800995, 1.8379957675933838));
        eventRecording.addEvent(new ClickEvent(10712, 0.8252184987068176, 0.20530834794044495, ViewEnum.GENERIC, "text", ""));

        final JSONObject expected = new JSONObject("{\n" +
                "    \"activity_name\": \"activities.DiaryMainActivity\",\n" +
                "    \"start_time\": 0,\n" +
                "    \"events\": [\n" +
                "        [\"s\", 2058, 0.5417786836624146, 0.7651091814041138, -0.0015757083892822266, 0.013917083851993084],\n" +
                "        [\"s\", 2207, 0.5841091871261597, 0.5248535871505737, -0.006710481829941273, 0.028040390461683273],\n" +
                "        [\"o\", 8350, 1],\n" +
                "        [\"f\", 9929, 0.7540574669837952, 0.7047873735427856, 0.3126640021800995, 1.8379957675933838],\n" +
                "        [\"c\", 10712, 0.8252184987068176, 0.20530834794044495, 1, \"text\", \"\"]\n" +
                "    ]\n" +
                "}");

        assertEquals(expected.toString(), eventRecording.toJson().toString());
    }
}
