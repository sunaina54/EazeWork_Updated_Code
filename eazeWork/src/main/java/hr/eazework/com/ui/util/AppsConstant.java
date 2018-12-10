package hr.eazework.com.ui.util;

import android.graphics.Color;
import android.graphics.Typeface;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 7/17/2016.
 * */
public class AppsConstant {


    public static String RESUBMIT="Resubmit",RETURN="Return",CLOSE="Close",REDIRECT="Redirect",
            DELETE="Delete",SAVE_DRAFT="Save",SUBMIT="Submit",
            APPROVE="Approve",REJECT="Reject",WITHDRAW="Withdraw",DRAFT="Draft",PRESENT="Present",ABSENT="Absent",LEAVE_EDIT="3",
            LEAVE_WITHDRAWAL="5",TOUR_EDIT="11",TOUR_WITHDRAWAL="12",VIEW_ACTION="V",EDIT_ACTION="E"
            ,OD_EDIT="32",OD_WITHDRAWAL="33",WFH_EDIT="13",WFH_WITHDRAWAL="14";

    public  static  final String SUCCESS="0";
    public  static  final String TEXT_ANNOUNCEMENT="11";
    public  static  final String SIMPLE_VIEW="S";
    public  static  final String ADVANCE_VIEW="A";
    public  static  final String Project_NAME="EazeWork";


    public static final int ADD_EXPENSE_CLAIM_FRAGMENT =1;
    public static final int VIEW_EDIT_EXPENSE_CLAIM_FRAGMENT =2;
    public static final int APPROVE_EDIT_EXPENSE_CLAIM_FRAGMENT =3;

    public static String KEY="&key=AIzaSyDS2Z6-lVqQXcly5xxBXJx3Kft5U_ecsPo";
    public static final int PRIORITY_MEDIUM = 2;
    public static final String GEOFENCE_ACTION = "hr.eazework.com.ACTION_GEOFENCE_RECEIVER";
    public static final String ISFROMSPLASH = "isFromSplash";
    public static final String ISFROMLOGIN = "isFromLogin";
    public static final String FCM_TOKEN = "fcmToken";
    public static String OFFICE_ID = "OfficeID";
    public static boolean isProduction=false;
    public static boolean isDebug=false;
    public static final int FRONT_CAMREA_OPEN = 1;
    public static final int BACK_CAMREA_OPEN = 2;
/*public static final int FRONT_CAMREA_OPEN = 0;
    public static final int BACK_CAMREA_OPEN = 1;*/
    public static String POSITION_KEY = "positionKey";
    public static String HISTORY_KEY = "historyKey";
    public static String EMP_ID = "EmpID";
    public static String ATTEND_ID = "attendID";
    public static String OPTION_SELECTED = "optionSelected";
    public static String MARK_DATE = "markDate";
    public static String SELFIE = "selfieYN";
    public static String GEOFENCE = "geofenceYN";
    public static String DATE_KEY = "DATE_KEY";
    public static int DEVICE_DISPLAY_HEIGHT_DEFAULT = 800;
    public static int DEVICE_DISPLAY_WIDTH_DEFAULT = 400;
    public static String DEFAULTRADIUS = "0";
    public static String DELETE_FLAG="D";
    public static String UPDATE_FLAG="U";
    public static String OLD_FLAG="O";
    public static String NEW_FLAG="N";
    public static Typeface RobotoSlabRegular;
    public static String URL ="URL";
    public static String USER ="USER";
    public static String PASSWORD ="PASSWORD";
    public static String FILE ="FILE";
    public static String FILEPURPOSE ="FILEPURPOSE";
    public static String METHOD ="METHOD";
    public static String REASON ="REASON";
    public static String PARAMS ="PARAMS";
    public static String GET ="GET";
    public static String GEOEVENT = "GeoEvent";
    public static String ORGANIZATIONID ="ORG_OPPO";
    public static String PUT = "PUT";
    public static String POST ="POST";
    public static final int IMAGE_PHOTO = 2;
    public static final int IMAGE_AADHAR = 3;
    public static final int IMAGE_ADDRESS_PROOF = 4;
    public static final int IMAGE_STORE = 5;
    public static int RunningLoaderCount =0;
    public static String TAG = "lstech.aos.debug";
    public static String LOCATION_UPDATE = "LOCATION_UPDATE";
    public static final List<String> offlineAttendanceStatuses = new ArrayList<>();
    public final static int REQ_CAMERA = 1003;

    public static String DATE_FORMATE="dd/MM/yyyy";
    public static String DATE_YEAR_MONTH_FORMAT="MMM yyyy";
    public static int PERIODIC_EXPENSE=4;
    public static int IMAGE_QUALITY=100;
    public static String PENDING = "P";
    public static String APPROVED = "A";

    public static String SELF = "S";
    public static String OTHER = "O";
    public static String YES = "Y";
    public static String NO = "N";

    public static String CLASSICAL_TOUR ="C017000024";
    public static String ADVENTURE="C017000025";
    public static String FAMILY_PACKAGE="C017000026";
    public static String STUDENT_SPECIAL="C017000027";
    public static String RELIGIOUS_TRAVEL="C017000029" ;
    public static String PHOTOGRAPHY="C017000030";

    public static String TOUR_TEXT = "1";
    public static String TOUR_NUMBER="2";
    public static String TOUR_DATE="3";
    public static String TOUR_DROPDOWN="4";


    public static String OD="OD";
    public static String WFH="Work from home" ;
    public static String TOUR="Tour";
    public static String TIME_MODIFICATION="Time Modification";
    public static String BACK_DATED_ATTENDANCE="Attendance";
    public static String SAVE_AS_DRAFT="Save";

    public static String PENDING_APPROVAL="PendingActivityFragment";


    public final static int GEOFENCE_STROKE_COLOR = Color.argb(50, 0, 127, 255);
    public final static int GEOFENCE_FILL_COLOR = Color.argb(100, 137, 207, 240);
    public final static String ADVANCE="AdvanceHome";
    public  final static String EXOENSE="ExpenseHome";
    static{
        offlineAttendanceStatuses.add("InLocation");
        offlineAttendanceStatuses.add("OutLocation");
    }
    public static  String EDIT="EDIT",ADD="ADD",VIEW="VIEW";
    public static String TICKET_ACCESS_SIMPLE ="TICKET_SIMPLE";
    public static String TICKET_ACCESS_ADVANCE = "TICKET_ADVANCE";
    public static String TICKET_ACCESS_BOTH = "B";
    public static String TICKET_ACCESS_KEY = "TICKET_ACCESS_KEY";
    public static String TICKET_SELF= "Ticket (Self)";
    public static String TICKET_Other = "Ticket (Other)";
    public static String TICKET_KEY = "TICKET_KEY";
    public static String TICKET_MENU_ACCESS = "TICKET_MENU_ACCESS";

    //Screen Number

    public static final String Login="MBL1.0";
    public static final String Application_Menu="MBL1.1";  // Not a page
    public static final String Home ="MBL1.2";
    public static final String Change_password="MBL1.3";
    public static final String Profile="MBL1.4";
    public static final String Leave_On_HomePage="MBL2.1"; // Not a page
    public static final String Leave_Request="MBL2.2";
    public static final String Leave_Home="MBL2.3";
    public static final String Leave_View="MBL2.4";
    public static final String Payslip_selection="MBL3.1";
    public static final String Payslip_View="MBL3.2";
    public static final String Download_Screen="MBL3.3"; // Not a page
    public static final String Attendance_On_HomePage="MBL4.1"; // Not a page
    public static final String Attendance_Map="MBL4.1.1";
    public static final String Attendance_Photo="MBL4.1.2";
    public static final String Attendance_History="MBL4.2";
    public static final String Attendance_Track="MBL4.3";
    public static final String Calendar_Screen="MBL4.4";
    public static final String Time_and_Attendance_Home="MBL4.7";
    public static final String Backdated_Attendance_Request="MBL4.5";
    public static final String Backdated_Attendance_View="MBL4.8";
    public static final String Time_Modification_Request="MBL4.6";
    public static final String Time_Modification_View="MBL4.9";
    public static final String Location_On_Homepage="MBL5.1"; //Not a page
    public static final String Location_Home="MBL5.2";
    public static final String Add_Edit_Location="MBL5.3";
    public static final String Location_Picture="MBL5.4";
    public static final String Teams_On_Homepage="MBL6.1"; // Not a page
    public static final String Team_Members="MBL6.2";
    public static final String Profile_Edit="MBL6.3";
    public static final String Member_Attendance_History="MBL6.4";
    public static final String Member_Reportee_Team="MBL6.5";
    public static final String Create_Employee="MBL7.1";
    public static final String Employee_Picture="MBL7.1.1";
    public static final String New_Employee_Approval="MBL7.2";
    public static final String Approvals_On_Homepage="MBL8.1"; // not a page
    public static final String Approvals_Home="MBL8.2";
    public static final String Leave_Approval="MBL8.2.1";
    public static final String Employee_Approval="MBL8.2.2";
    public static final String Advance_Approval="MBL8.2.3";
    public static final String Expense_Approval="MBL8.2.4";
    public static final String Attendance_Approval="MBL8.2.5";
    public static final String Open_Tickets="MBL8.2.6";
    public static final String Advance_Home="MBL9.1";
    public static final String Advance_Request="MBL9.2";
    public static final String Advance_View="MBL9.3";
    public static final String Expense_Home="MBL10.1";
    public static final String Expense_Request="MBL10.2";
    public static final String Expense_View="MBL10.3";
    public static final String Tour_Request="MBL11.1";
    public static final String Tour_View="MBL11.2";
    public static final String WFH_Request="MBL11.3";
    public static final String WFH_View="MBL11.4";
    public static final String OD_Request="MBL11.5";
    public static final String OD_View="MBL11.6";
    public static final String Ticket_Home="MBL12.1";
    public static final String Ticket_Creation="MBL12.2";
    public static final String Ticket_View="MBL12.3";



}
