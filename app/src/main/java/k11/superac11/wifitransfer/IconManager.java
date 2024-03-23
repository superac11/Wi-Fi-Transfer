package k11.superac11.wifitransfer;

import java.util.HashMap;
import java.util.Map;

public class IconManager {
    private static final Map<String, String> FILE_TYPE_ICONS = new HashMap<>();

    static {
        FILE_TYPE_ICONS.put("folder", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"48\" height=\"48\" fill=\"currentColor\" class=\"bi bi-folder\" viewBox=\"0 0 16 16\"><path d=\"M.5 1a.5.5 0 0 1 .5.5V5l.348.02c.286.02.644.066 1.018.154.677.155 1.367.44 1.777.808C4.09 6.657 4.293 7 4.5 7h7c.207 0 .41-.343.355-.618.41-.368 1.1-.653 1.777-.808.374-.088.732-.134 1.018-.154L16 5V1.5a.5.5 0 0 1 1 0V14a2 2 0 0 1-2 2H1a2 2 0 0 1-2-2V1.5a.5.5 0 0 1 .5-.5z\"/></svg>");
        FILE_TYPE_ICONS.put("file", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"48\" height=\"48\" fill=\"currentColor\" class=\"bi bi-file\" viewBox=\"0 0 16 16\"><path d=\"M12.5 0a.5.5 0 0 1 .5.5V7h-1V1H8v14h3.5a.5.5 0 0 1 0 1h-7a.5.5 0 0 1 0-1H5V1H1.5a.5.5 0 0 1-.5-.5V0h11zM6 9h1v1H6v-1zm0-3h1v1H6V6zm0-2h1v1H6V4zm0-2h1v1H6V2zm3 8h5v2H9v-2zm0-3h5v2H9V7z\"/></svg>");
        FILE_TYPE_ICONS.put("audio", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-music-note\" viewBox=\"0 0 16 16\"><path d=\"M0 14c0 1.1.9 2 2 2s2-.9 2-2-2-.9-2-2 2-.9 2-2 2 .9 2 2-2 .9-2 2H0zM12 3v4a1 1 0 0 1-1.5.87L8 6.36V14h1a1 1 0 0 1 1 1v1a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-1a1 1 0 0 1 1-1h2a1 1 0 0 1 1-1V6.57l2.5 1.51A1 1 0 0 1 14 8V3h-2z\"/></svg>");
        FILE_TYPE_ICONS.put("video", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-camera-video\" viewBox=\"0 0 16 16\"><path d=\"M4.5 1a.5.5 0 0 1 .5.5V4h8V1.5a.5.5 0 0 1 1 0V14a.5.5 0 0 1-1 0V11H4v2.5a.5.5 0 0 1-1 0V1.5a.5.5 0 0 1 .5-.5zM1 3v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1z\"/><path d=\"M13.037 5.5a1 1 0 0 1 1.52.864l.63 3a1 1 0 0 1-1.342 1.16l-2.263-.842a.5.5 0 0 0-.34 0L8.78 11.64a1 1 0 0 1-1.342-1.16l.63-3a1 1 0 0 1 1.52-.864L12 8.332l1.037-2.832z\"/></svg>");
        FILE_TYPE_ICONS.put("text", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-file-text\" viewBox=\"0 0 16 16\"><path d=\"M11.742 1a2 2 0 0 1 1.415.586l2.829 2.828A2 2 0 0 1 16 5.343V14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V1.998L8.243 1H11.742zM11 3h2v2h-2V3zm0 3h2v2h-2V6zm0 3h2v2h-2V9zm0 3h2v2h-2v-2zM2 11h2v2H2v-2zm0 3h2v1H2v-1z\"/></svg>");
        FILE_TYPE_ICONS.put("image", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-image\" viewBox=\"0 0 16 16\"><path d=\"M15.5 1a.5.5 0 0 1 .5.5V14a.5.5 0 0 1-1 0V2a.5.5 0 0 1 .5-.5zM14 1H2a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm-7.778 11.467a5.5 5.5 0 1 1 6.95 0l-1.46-1.088a4.5 4.5 0 1 0-4.03 0L6.222 12.467zM6 10a2 2 0 1 1 0-4 2 2 0 0 1 0 4z\"/></svg>");
        FILE_TYPE_ICONS.put("doc", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-file-word\" viewBox=\"0 0 16 16\"><path d=\"M14 2H7a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h1v2l1-1h4a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1zm-1 10H8V8h5v4z\"/></svg>");
        FILE_TYPE_ICONS.put("slideshow", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-file-earmark-play\" viewBox=\"0 0 16 16\"><path d=\"M8 2a6 6 0 1 0 0 12A6 6 0 0 0 8 2zm.5 11V3l5.5 5.5-5.5 5.5z\"/></svg>");
        FILE_TYPE_ICONS.put("pdf", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-file-pdf\" viewBox=\"0 0 16 16\"><path d=\"M1.92 0A1.92 1.92 0 0 0 0 1.92V14.4c0 1.058.86 1.92 1.92 1.92H8v-1.5H1.5V1.92C1.5.861 2.36 0 3.42 0H12v1.5h1.5V14.4c0 1.058-.86 1.92-1.92 1.92H3.42A1.92 1.92 0 0 1 1.5 14.4V1.92C1.5.861 2.36 0 3.42 0H16v1.5h-1.5V14.4z\"/></svg>");
        FILE_TYPE_ICONS.put("powerpoint", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-file-ppt\" viewBox=\"0 0 16 16\"><path d=\"M7.5 0A5.5 5.5 0 0 1 13 5.5a.5.5 0 0 1-1 0A4.5 4.5 0 0 0 7.5 1a.5.5 0 0 1 0-1zM6 1v3.5a.5.5 0 0 1-1 0V1a1 1 0 0 1 1-1zm9 4a.5.5 0 0 1 0 1H13v9a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V6H1.5a.5.5 0 0 1 0-1A5.5 5.5 0 0 1 7 0a.5.5 0 0 1 0 1 4.5 4.5 0 0 0-9 0 .5.5 0 0 1 0-1A5.5 5.5 0 0 1 14 5.5z\"/></svg>");
    }

    static String getIcon(String fileType) {
        return FILE_TYPE_ICONS.getOrDefault(fileType, "");
    }
}
