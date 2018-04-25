package org.demo.autotarget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.autotarget.annotation.ActivityTarget;
import org.autotarget.generated.FragmentTargets;
import org.autotarget.service.JTargetService;
import org.autotarget.service.TargetService;
import org.autotarget.util.ContextInjector;

@ActivityTarget
public class JavaActivity extends AppCompatActivity {

    private JTargetService targetService = new JTargetService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextInjector.INSTANCE.inject(this);

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
