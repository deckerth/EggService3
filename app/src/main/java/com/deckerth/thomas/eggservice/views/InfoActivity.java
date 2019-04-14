package com.deckerth.thomas.eggservice.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.widget.TextView;

import com.deckerth.thomas.eggservice.R;

import static android.text.Html.fromHtml;

public class InfoActivity extends AppCompatActivity {

    private static final String INFO_DE = "" +
            "<p>&copy; 2019 Thomas Decker (deckerth@gmail.com)</p>" +
            "<p>&nbsp;</p>" +
            "<p>Die folgenden St&uuml;cke sind unter der Lizenz \"Creative Commons Attribution\" " +
            "(https://creativecommons.org/licenses/by/4.0/) lizenziert.<br/> " +
            "Sie wurden gek&uuml;rzt um als Alarmton dienen zu k&ouml;nnen.</p> " +
            "<p>" +
            "<ul> " +
            "<li><strong>&nbsp;Divertimento K131</strong> von <em>Kevin MacLeod</em>.  " +
            "Quelle: http://incompetech.com/music/royalty-free/index.html?isrc=USUAN1100533, K&uuml;nstler: http://incompetech.com/</li> " +
            "<li><strong>&nbsp;Walk&uuml;renritt</strong> (Wagner), <strong>Polowetzer T&auml;nze " +
            "</strong> (F&uuml;rst Igor, Borodin), <strong>Brindisi</strong> (La Traviata, Verdi): " +
            "Aus dem Album \"An Opera Evening\" mit dem <em>MIT Symphony Orchestra</em>. " +
            "Quelle: https://freemusicarchive.org/music/MIT_Symphony_Orchestra/An_Opera_Evening/</li> " +
            "</ul> " +
            "</p> ";

    private static final String INFO_EN = "" +
            "<p>&copy; 2019 Thomas Decker (deckerth@gmail.com)</p>" +
            "<p>&nbsp;</p>" +
            "<p>The following titles are licenced under the \"Creative Commons Attribution\" licence &nbsp;" +
            "(https://creativecommons.org/licenses/by/4.0/).<br/> " +
            "They have been shortened to serve as alarm sounds.</p> " +
            "<p>" +
            "<ul> " +
            "<li><strong>&nbsp;Divertimento K131</strong> by <em>Kevin MacLeod</em>.  " +
            "Source: http://incompetech.com/music/royalty-free/index.html?isrc=USUAN1100533, Artist: http://incompetech.com/</li> " +
            "<li><strong>&nbsp;Ride of the Valkyries</strong> (Wagner), <strong>Polovetsian Dances" +
            "</strong> (Prince Igor, Borodin), <strong>Brindisi</strong> (La Traviata, Verdi): " +
            "From the album \"An Opera Evening\" by the <em>MIT Symphony Orchestra</em>. " +
            "Source: https://freemusicarchive.org/music/MIT_Symphony_Orchestra/An_Opera_Evening/</li> " +
            "</ul> " +
            "</p> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView richTextBox = findViewById(R.id.info_text);
        Spanned text;
        if (getString(R.string.yes).contentEquals("Ja")) {
            text = fromHtml(INFO_DE);
        } else {
            text = fromHtml(INFO_EN);
        }
        richTextBox.setText(text);
    }
}
