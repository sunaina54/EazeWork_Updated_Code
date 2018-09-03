package hr.eazework.com.ui.fragment.Ticket;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.EmployeeLeaveModel;
import hr.eazework.com.model.GetWFHRequestDetail;
import hr.eazework.com.model.LeaveReqsItem;
import hr.eazework.com.model.LeaveRequestDetailsModel;
import hr.eazework.com.model.MenuItemModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.ui.adapter.DocumentUploadAdapter;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;

/**
 * Created by SUNAINA on 22-08-2018.
 */

public class ViewTicketFragment extends BaseFragment {
    private Context context;
    public static final String TAG = "ViewTicketFragment";
    private String screenName = "ViewTicketFragment";
    private Preferences preferences;
    private TextView requestIdTV, empNameTV, statusTV, startDateTV, endDateTV, daysTV, dateWorkedTV;
    private TextView submittedByTV, pendingWithTV;
    private LinearLayout remarksLinearLayout, wfhSummaryLl, tourSummaryLl, odSummaryLl, docLl;
    private RecyclerView remarksRV;
    private Button withdrawBTN, clickBTN;
    private LinearLayout errorLinearLayout;
    private DocumentUploadAdapter documentViewAdapter;
    private RecyclerView documentRV;
    private ImageView plus_create_newIV;
    private EmployeeLeaveModel employeeLeaveModel;
    private LeaveReqsItem leaveReqsItem;
    private LinearLayout dateWorkedLl;

    private View progressbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setShowPlusMenu(true);
        this.setShowEditTeamButtons(false);

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.view_ticket_fragment, container, false);
        context = getContext();
        preferences = new Preferences(getContext());
        setupScreen();
        return rootView;
    }

    private void setupScreen() {
        context = getActivity();
        preferences = new Preferences(getContext());
        int textColor = Utility.getTextColorCode(preferences);
        ((TextView) getActivity().findViewById(R.id.tv_header_text)).setTextColor(textColor);
        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_header_text)).setText(R.string.view_tour_summary);
        ((MainActivity) getActivity()).findViewById(R.id.ibRight).setVisibility(View.GONE);
        ((MainActivity) getActivity()).findViewById(R.id.ibWrong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();
                if (menuItemModel != null) {
                    MenuItemModel itemModel = menuItemModel.getItemModel(MenuItemModel.WORK_FROM_HOME);
                    if (itemModel != null && itemModel.isAccess()) {
                        mUserActionListener.performUserAction(IAction.WORK_FROM_HOME_SUMMARY, null, null);
                    } else {
                        mUserActionListener.performUserAction(IAction.WORK_FROM_HOME_SUMMARY, null, null);
                    }
                }
            }
        });
        documentRV = (RecyclerView) rootView.findViewById(R.id.expenseRecyclerView);
        errorLinearLayout = (LinearLayout) rootView.findViewById(R.id.errorDocTV);
        errorLinearLayout.setVisibility(View.VISIBLE);
        progressbar = (LinearLayout) rootView.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        docLl = (LinearLayout) rootView.findViewById(R.id.docLl);
        plus_create_newIV = (ImageView) rootView.findViewById(R.id.plus_create_newIV);
        plus_create_newIV.setVisibility(View.GONE);
        wfhSummaryLl = (LinearLayout) rootView.findViewById(R.id.wfhSummaryLl);
        wfhSummaryLl.setVisibility(View.VISIBLE);
        tourSummaryLl = (LinearLayout) rootView.findViewById(R.id.tourSummaryLl);
        tourSummaryLl.setVisibility(View.GONE);
        odSummaryLl = (LinearLayout) rootView.findViewById(R.id.odSummaryLl);
        odSummaryLl.setVisibility(View.GONE);

        remarksLinearLayout = (LinearLayout) rootView.findViewById(R.id.remarksLinearLayout);
        remarksRV = (RecyclerView) rootView.findViewById(R.id.remarksRV);

        //First
        requestIdTV = (TextView) rootView.findViewById(R.id.requestIdTV);
        empNameTV = (TextView) rootView.findViewById(R.id.empNameTV);
        statusTV = (TextView) rootView.findViewById(R.id.statusTV);
        dateWorkedLl = (LinearLayout) rootView.findViewById(R.id.dateWorkedLl);
        dateWorkedTV = (TextView) rootView.findViewById(R.id.dateWorkedTV);
        submittedByTV = (TextView) rootView.findViewById(R.id.submittedByTV);
        pendingWithTV = (TextView) rootView.findViewById(R.id.pendingWithTV);
        startDateTV = (TextView) rootView.findViewById(R.id.startDateTV);
        endDateTV = (TextView) rootView.findViewById(R.id.endDateTV);
        daysTV = (TextView) rootView.findViewById(R.id.daysTV);




    }
}
