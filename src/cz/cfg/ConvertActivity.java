package cz.cfg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConvertActivity extends Activity {
    /** Called when the activity is first created. */
	
	private EditText entryText;
	private Spinner spinner;
	private Button okButton;
	private TransformationTypes type;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setTitle(R.string.app_name);
        
        entryText = (EditText) findViewById(R.id.entry);
        spinner = (Spinner) findViewById(R.id.spinner);
        okButton = (Button) findViewById(R.id.ok);
        type = TransformationTypes.NE1;
        
        //Dropdown menu s výbìrem konverze
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.convertArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }
    
    public void convert(View view) {
    	okButton.setEnabled(false);
		Parser parser = new Parser();
		CFGConvertor converter = new CFGConvertor();
		String cfgString = entryText.getText().toString();
		String[] convertedCFG = null;
		try {
			convertedCFG = converter.convert(parser.parse(cfgString), type, parser.orderingOfNonTerminals(cfgString));
		} catch (ParserException e) {
			Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		if (convertedCFG != null) {
			Intent i = new Intent(ConvertActivity.this, ConvertedActivity.class);
    		i.putExtra("CFG", convertedCFG);
    		i.putExtra("oldCFG", cfgString);
    		okButton.setEnabled(true);
			startActivity(i);
		} else {
			okButton.setEnabled(true);
		}
    }
    
    public void addArrow(View view) {
    	insertText("->");
    }
    
    public void addLine(View view) {
    	insertText("|");
    }
    
    public void addEps(View view) {
    	insertText("\\e");
    }
    
    private void insertText(String symbol) {
    	int pos = entryText.getSelectionStart();
    	String text = entryText.getText().toString();
    	String prefix = text.substring(0, pos);
    	String sufix = text.substring(pos);
    	entryText.setText(prefix + symbol + sufix);
    	entryText.setSelection(pos + symbol.length());
    }
    
    public void clear(View view) {
    	entryText.setText("");
    }
    
    private class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	switch (pos) {
        	case 0: type = TransformationTypes.NE1;
        			break;
        	case 1: type = TransformationTypes.NE2;
        			break;
        	case 2: type = TransformationTypes.RED;
        			break;
        	case 3: type = TransformationTypes.EPS;
        			break;
        	case 4: type = TransformationTypes.SRF;
        			break;
        	case 5: type = TransformationTypes.PRO;
        			break;
        	case 6: type = TransformationTypes.CNF;
        			break;
        	case 7: type = TransformationTypes.RLR;
        			break;
        	case 8: type = TransformationTypes.GNF;
					break;
        	default:type = TransformationTypes.NE1; 
        			break;
        	}
        	Toast.makeText(parent.getContext(), "Konverze: " +
        	     parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        @SuppressWarnings("rawtypes")
		public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
}