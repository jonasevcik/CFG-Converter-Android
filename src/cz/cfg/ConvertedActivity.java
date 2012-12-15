package cz.cfg;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class ConvertedActivity extends Activity{
	
	private EditText text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Vytáhneme data a nastavíme titulek a zobrazíme gramatiku
		String [] convertedStrings = getIntent().getStringArrayExtra("CFG");
		
		setTitle(convertedStrings[0]);
		setContentView(R.layout.converted);
		
		text = (EditText) findViewById(R.id.convertedText);

		text.setText(convertedStrings[1]);
	}
}
