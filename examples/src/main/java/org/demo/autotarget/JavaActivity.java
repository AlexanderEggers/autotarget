package org.demo.autotarget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import archknife.context.ContextProvider;
import autotarget.annotation.ActivityTarget;
import autotarget.generated.FragmentTargets;
import autotarget.service.TargetService;

@ActivityTarget
public class JavaActivity extends AppCompatActivity {

    private TargetService targetService = new TargetService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextProvider.INSTANCE.setContext(this);

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
