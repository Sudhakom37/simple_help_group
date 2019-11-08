package com.luxand.shg.api;


import com.google.gson.JsonObject;
import com.luxand.shg.helper.Twin;
import com.luxand.shg.helper.Twin_uuid;
import com.luxand.shg.model.AddLoan;
import com.luxand.shg.model.Attendance;
import com.luxand.shg.model.Auth;
import com.luxand.shg.model.BarChartModel;
import com.luxand.shg.model.Caste;
import com.luxand.shg.model.DashBoard;
import com.luxand.shg.model.DefaultersModel;
import com.luxand.shg.model.DepartmentDashboardModel;
import com.luxand.shg.model.FeedBackResult;
import com.luxand.shg.model.Feedback;
import com.luxand.shg.model.FeedbackModelVO;
import com.luxand.shg.model.FeedbacksModel;
import com.luxand.shg.model.FrKey;
import com.luxand.shg.model.GroupDatfile;
import com.luxand.shg.model.GroupLoan;
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.model.GroupUserLoan;
import com.luxand.shg.model.GroupsModel;
import com.luxand.shg.model.HomeModel;
import com.luxand.shg.model.Login;
import com.luxand.shg.model.Meeting;
import com.luxand.shg.model.MeetingDetailsModel;
import com.luxand.shg.model.MeetingPO;
import com.luxand.shg.model.MeetingPhotosModel;
import com.luxand.shg.model.OtpModel;
import com.luxand.shg.model.Registration;
import com.luxand.shg.model.RepayLoan;
import com.luxand.shg.model.RootMapModel;
import com.luxand.shg.model.SanctionLoanModel;
import com.luxand.shg.model.SavingsModel;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.model.TipsModel;
import com.luxand.shg.model.TrainingVideoModel;
import com.luxand.shg.model.UserSavings;
import com.luxand.shg.model.Users;
import com.luxand.shg.model.Village;
import com.luxand.shg.model.VillageOfDashBoard;

import java.util.List;

import io.reactivex.Completable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface Api {

    @POST("oauth/token")
    Observable<Auth> getBearerKey(@Body JsonObject object);
    @GET("login/get_frkey")
    Observable<FrKey> getFrKey(@Query("mac_address") String macAddress);


    @POST("login/verify_pin")
    Observable<Login> loginWithPin(@Body JsonObject object);
    @POST("login/verify_mobile")
    Observable<Registration> registerWithNumber(@Body JsonObject object);
    @POST("login/verify_otp")
    Observable<OtpModel> verifyOtp(@Body JsonObject object);

    @POST("login/uploadimage")
    Observable<Registration> uploadImageAndDat(@Body RequestBody body);

    @POST("login/update_pin")
    Observable<Registration> updatePin(@Body JsonObject body);

    @GET("menu_options")
    Observable<HomeModel> getHomeItems(@Query("role_id") String rollNumber);
    @GET("group/members_list")
    Observable<GroupMember> getGroupMemberList(@Query("group_id") String group_id);

    @GET("group/group_duplicates")
    Observable<Twin> getClassIdenticalStudents(@Query("group_id") String id);

    @GET("group/mapped_users")
    Observable<Twin_uuid> getIdenticalStudents(@Query("uuid") String id);

    @POST("group/duplicate_mapping")
    Observable<SuccessModel> duplicateMap(@Body RequestBody body);

    @POST("group/save_student_attendance")
    Observable<Attendance> Save_Member_Attendance(@Body JsonObject object);


    @POST("group/save_meetings_attachments")
    Observable<MeetingPhotosModel> save_meetings_attachments(@Body RequestBody object);

    @POST("group/enroll_members")
    Observable<SuccessModel> SendEnrolledStudentsToServer(@Body RequestBody body);

    @GET("group/checklock")
    Observable<SuccessModel> checkLock(@Query("type") String type,
                                       @Query("group_id") String group_id,
                                       @Query("mac_address") String mac_address);

    @GET("group/download_group_dat")
    Observable<GroupDatfile> download_staff_dat(@Query("group_id") String school_id,
                                                @Query("mac_address") String mac_address);

    @Streaming
    @retrofit2.http.GET
    Observable<Response<ResponseBody>> downloadFile(@Url String fileUrl);

    @GET("group/update_group_dat")
    Observable<SuccessModel> updateDownloadDatStatus(@Query("group_id") String school_id,
                                                        @Query("mac_address") String mac_address);

    @GET("group/meetings_list")
    Observable<Meeting> getMeetingList(@Query("group_id") String group_id);


    @POST("group/save_meetings_feedbacks")
    Observable<FeedBackResult> saveMeetingsFeedback(@Body RequestBody body);


    @GET("group/meetings_details")
    Observable<MeetingDetailsModel> getMeetingDetails(@Query("group_id") String group_id, @Query("meetings_id") String meeting_id);
    @GET("group/dashboard")
    Observable<DashBoard> getDashBoard(@Query("group_id") String group_id);

    @GET("group/savings_list")
    Observable<UserSavings> getGroupSavings(@Query("group_id") String group_id);
    @GET("group/savings_details")
    Observable<UserSavings> getGroupSavingDetails(@Query("group_id") String group_id, @Query("user_id") String user_id);

    @GET("group/group_loans")
    Observable<GroupLoan> getGroupLoans(@Query("group_id") String group_id);
    @GET("group/loan_users")
    Observable<GroupUserLoan> getGroupLoanUsers(@Query("group_loan_id") String loan_id, @Query("type") String type);
    @GET("group/loan_user_paid")
    Observable<UserSavings> getGroupLoanPaidUsers(@Query("loan_user_id") String loan_id);

    @GET("group/business_expansion")
    Observable<TipsModel> getTipsList(@Query("group_id") String group_id);
    @GET("group/trainingvideos")
    Observable<TrainingVideoModel> getTrainingVideosList(@Query("group_id") String group_id);
    @GET("group/defaulterslist")
    Observable<DefaultersModel> getDefaultersList(@Query("group_id") String group_id);
    @GET("group/members_list")
    Observable<AddLoan> getMemberList(@Query("group_id") String group_id);
    @GET("group/loanusers")
    Observable<RepayLoan> getLoanUsersList(@Query("group_loan_id") int group_loan_id);

    @POST("group/adduserloan")
    Observable<SuccessModel> addLoan(@Body JsonObject body);
    @POST("group/loanrepayment")
    Observable<SuccessModel> repayLoan(@Body JsonObject body);

    @GET("caste")
    Observable<Caste> getCastes();
    @GET("villages")
    Observable<Village> getVillages();

    @POST("group/addmember")
    Observable<SuccessModel> addGroupMember(@Body JsonObject body);
    @POST("group/addgroup")
    Observable<SuccessModel> addGroup(@Body JsonObject body);


    // Village office services
    @GET("village/dashboard")
    Observable<VillageOfDashBoard> getVillageOfficerDashboard(@Query("village_id") String village_id);
    @GET("village/groupmeetings")
    Observable<MeetingPO> getMeetingListVO(@Query("group_id") String group_id);

    @GET("village/groupfeedbacks")
    Observable<FeedbackModelVO> getFeedbackListVO(@Query("group_id") String group_id);


    @GET("village/meetingfeedbacks")
    Observable<Feedback> getFeedbackDetailsVO(@Query("meetings_id") String meetings_id);

    // Department Services
    @GET("department/dashboard")
    Observable<DepartmentDashboardModel> getDepartmentHomeItems();
    // Active Groups levels
    @GET("department/activegroups")
    Observable<BarChartModel> getDepartmentActiveGroups();
    @GET("department/activegroups")
    Observable<BarChartModel> getDepartmentActiveGroups(@Query("district_id") String district_id);
    @GET("department/activegroups")
    Observable<BarChartModel> getDepartmentActiveGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id);
    @GET("department/activegroups")
    Observable<BarChartModel> getDepartmentActiveGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);


    // InActive Groups levels
    @GET("department/inactivegroups")
    Observable<BarChartModel> getDepartmentInActiveGroups();
    @GET("department/inactivegroups")
    Observable<BarChartModel> getDepartmentInActiveGroups(@Query("district_id") String district_id);
    @GET("department/inactivegroups")
    Observable<BarChartModel> getDepartmentInActiveGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id);
    @GET("department/inactivegroups")
    Observable<BarChartModel> getDepartmentInActiveGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);

    // Defaulter Groups levels
    @GET("department/defaulterslist")
    Observable<BarChartModel> getDepartmentDefaulterGroups();
    @GET("department/defaulterslist")
    Observable<BarChartModel> getDepartmentDefaulterGroups(@Query("district_id") String district_id);
    @GET("department/defaulterslist")
    Observable<BarChartModel> getDepartmentDefaulterGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id);
    @GET("department/defaulterslist")
    Observable<BarChartModel> getDepartmentDefaulterGroups(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);

    // Savings Groups levels
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentSavings();
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentSavings(@Query("district_id") String district_id);
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentSavings(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id);
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentSavings(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);
    // Savings Groups levels
/*    @GET("department/savings")
    Observable<BarChartModel> getDepartmentMeetings();
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentMeetings(@Query("district_id") String district_id);
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentMeetings(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id);*/
    @GET("department/savings")
    Observable<BarChartModel> getDepartmentMeetings(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);
    @GET("department/loanamount")
    Observable<BarChartModel> getDepartmentLoans(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);
    @GET("department/feedbackcount")
    Observable<BarChartModel> getDepartmentFeedback(@Query("district_id") String district_id,@Query("mandal_id") String mandal_id,@Query("village_id") String village_id);

    // Division Services

    @GET("division/groups_list")
    Observable<GroupsModel> getGroupList(@Query("user_id") String user_id);

    @GET("division/group_meetings")
    Observable<GroupsModel> getDivisionGroupMeetings(@Query("group_id") String group_id);
    @POST("division/face_recognition")
    Observable<SuccessModel> authenticateDivisionOfficer(@Body JsonObject object);
    @POST("division/start_meeting")
    Observable<SuccessModel> startMeeting(@Body JsonObject object);
    @POST("division/end_meeting")
    Observable<SuccessModel> stopMeeting(@Body JsonObject object);
    @GET("division/route_map")
    Observable<RootMapModel> loadMap(@Query("meetings_id") String meetings_id);
    @GET("group/group_feedback")
    Observable<FeedbacksModel> getDepartmentCommunicationData(@Query("group_id") String group_id);

    @POST("division/addloan")
    Observable<SuccessModel> sanctionLoans(@Body JsonObject body);
    @GET("division/group_loans")
    Observable<SanctionLoanModel> getSanctionAmount(@Query("user_id") String user_id);

}
