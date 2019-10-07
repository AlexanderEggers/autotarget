package org.demo.autotarget;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import autotarget.annotation.ActivityTarget;
import autotarget.generated.FragmentTargets;
import autotarget.service.TargetService;

@ActivityTarget
public class JavaActivity extends AppCompatActivity {

    private TargetService targetService = new TargetService(ContextProviderSingleton.getContextProvider());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextProviderSingleton.getContextProvider().setActivityContext(this);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setText("Show next fragment");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetService.execute(FragmentTargets.showFragmentDemo());
            }
        });
    }
}
