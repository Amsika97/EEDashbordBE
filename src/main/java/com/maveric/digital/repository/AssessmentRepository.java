package com.maveric.digital.repository;

import java.util.List;
import com.maveric.digital.model.embedded.AssessmentStatus;
import java.util.Optional;
import java.util.stream.Collectors;

import com.maveric.digital.responsedto.ConsulateAccountCountDto;
import com.maveric.digital.responsedto.ReportFilters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.ConsulateProjectCountDto;


@Repository
public interface AssessmentRepository extends MongoRepository<Assessment,Long> {
    List<Assessment> findBySubmitedByOrderByUpdatedAtDesc(String submitedBy);
    List<Assessment> findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(String submitedBy,Long from,Long to);
    List<Assessment> findByUpdatedAtBetweenOrderByUpdatedAtDesc(Long from,Long to);
     List<Assessment> findBySubmitedByAndSubmitStatus(String submittedBy,String submitStatus);
    @Query("{'submitStatus':'SUBMITTED'}")
    List<Assessment> findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort sort);
    @Query("{'submitStatus': ?0}")
	List<Assessment> findAllAssessmentsBySubmitStatus(String submitStatus);

    @Aggregation(pipeline = {
            "{'$match':{'submitedAt':{'$exists':true}}}",
            "{$group: {'_id': '$project', 'count': {$sum: 1}}}"
    })
    List<ConsulateProjectCountDto> countByProject();


    @Aggregation(pipeline = {
            "{'$match':{'submitedAt':{'$exists':true}}}",
            "{$group: {'_id': '$account', 'count': {$sum: 1}}}"
    })
    List<ConsulateAccountCountDto> countByAccount();
    @Query("{'id':?0,'reviewers.reviewerId':?1}")
    Assessment findAssessmentByAssessmentIdAndReviewerId(Long assessmentId,Integer reviewerId);
    @Aggregation(pipeline = { "{'$match': {'submitedAt':{'$gte':?0,'$lte':?1,'$exists':true},'projectTypeId': ?2,'submitStatus': { $in: ?3 } }}",
			"{'$sort':{'submitedAt':1}}",
			"{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submitedAt'}}},'count': { '$count': {} }}}" })
	List<LineChartProjection> findLineChartDataByStartAndEndDatesAndProjectTypeId(Long startDate, Long endDate,
                                                                                  Long projectTypeId,List<AssessmentStatus> assessmentStatusList);
     @Aggregation(pipeline = { "{'$match': {'submitedAt':{'$gte':?0,'$lte':?1,'$exists':true},'projectId': {'$in': ?2},'submitStatus': { $in: ?3 } }}",
			"{'$sort':{'submitedAt':1}}",
			"{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submitedAt'}}},'count': { '$count': {} }}}" })
	List<LineChartProjection> findLineChartDataByStartAndEndDatesInAndProjectId(Long startDate, Long endDate,
                                                      List<Long> projectId,List<AssessmentStatus> assessmentStatusList);
     @Aggregation(pipeline = { "{'$match': {'submitedAt':{'$gte':?0,'$lte':?1,'$exists':true},'accountId': ?2,'submitStatus': { $in: ?3 } }}",
			"{'$sort':{'submitedAt':1}}",
			"{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submitedAt'}}},'count': { '$count': {} }}}" })
	List<LineChartProjection> findLineChartDataByStartAndEndDatesAndAccount(Long startDate, Long endDate,
                                                                            Long accountId,List<AssessmentStatus> assessmentStatusList);
   @Aggregation(pipeline = { "{'$match': {'submitedAt':{'$gte':?0,'$lte':?1,'$exists':true},'submitStatus': { $in: ?2 } }}",
			"{'$sort':{'submitedAt':1}}",
			"{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submitedAt'}}},'count': { '$count': {} }}}" })
	List<LineChartProjection> findLineChartDataByStartAndEndDates(Long startDate, Long endDate,List<AssessmentStatus> assessmentStatus);
    List<Assessment> findTop5ByOrderByCreatedAtDesc(String submittedBy);
    List<Assessment> findBySubmitStatusInAndSubmitedBy(List<AssessmentStatus> assessmentStatusList, String submittedBy);
    List<Assessment> findTop10BySubmitStatusInOrderByUpdatedAtDesc(List<AssessmentStatus> assessmentStatusList);
    long count();
    Integer countBySubmitStatusIn(List<AssessmentStatus> statusList);
    List<Assessment> findAllBySubmitedByOrderByUpdatedAtDesc(String submittedBy);
    @Query("{'template.id': ?0, 'project.id': ?1}")
    List<Assessment> findByTemplateIdAndProjectId(Long templateId, Long projectId);

  @Query("{'projectCategory.templateQuestionnaire.fileUri': {$regex: ?0}}")
  Optional<Assessment> findAssessmentWithFileUri(String regexPattern);

  List<Assessment> findAllBySubmitedByNotAndSubmitStatusOrderByUpdatedAtDesc(String submittedBy,String submitStatus,Sort sort);
  
  List<Assessment> findBySubmitStatusIn(List<AssessmentStatus> status,Sort sort);
  @Query("{'businessUnit.id': ?0, 'submitStatus': { $in: ?1 }}")
  List<Assessment> findTop10ByBusinessUnitIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue,List<AssessmentStatus> assessmentStatusList);
  List<Assessment> findTop10ByAccountIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue,List<AssessmentStatus> assessmentStatusList);
  List<Assessment> findTop10ByProjectIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue,List<AssessmentStatus> assessmentStatusList);
  List<Assessment> findTop10ByProjectIdInAndSubmitStatusInOrderByUpdatedAtDesc(List<Long> filterValue,List<AssessmentStatus> assessmentStatusList);
  List<Assessment> findTop10ByProjectTypeIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue,List<AssessmentStatus> assessmentStatusList);


  List<Assessment> findByProjectIdIn(List<Long> id);
  @Query("{'projectType.id' : ?0}")
  List<Assessment> findByProjectTypeId(Long id);
  @Query("{'account.id' : ?0}")
  List<Assessment> findByAccountId(Long id);

    @Query("{'account.id' : ?0,'submitStatus': { $in: ?1 }}")
    List<Assessment> findByAccountIdAndSubmitStatusIn(Long id,List<AssessmentStatus> status);
  List<Assessment> findBySubmitStatusIn(List<AssessmentStatus> status);
  List<Assessment> findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus assessmentStatus,String reviewerId);


    List<Assessment> findByFrequencyReminderDateBetweenAndIsFrequencyRequiredTrue(Long startDate, Long endDate);

    List<Assessment> findByFrequencyOverDueRemindersDateBetweenAndIsFrequencyRequiredTrue(Long startDate, Long endDate);

    @Query(value = "{'id' : ?0}")
    @Update(value = "{'$set': {'frequencyRemindersSent': ?1}}")
    void updateFrequencyRemindersSent(Long id, List<Long> frequencyOverDueRemindersSent);
    @Query("{'submitedBy': ?0,'template.id': ?1, 'project.id': ?2,'id': { $ne: ?4 }}")
    @Update("{'$set': {'isFrequencyRequired': ?3}}")
    void findBySubmitedByAndTemplateIdAndProjectIdAndUpdateisFrequencyRequired(String submittedBy, Long templateId, Long projectId, boolean flag, Long id);


  Integer countByAccountIdAndSubmitStatusEquals(Long accountId, String status);

  Integer countByProjectIdInAndSubmitStatus(List<Long> projectIds, String status);

  Integer countBySubmitStatusEquals(String status);
  
  
  @Query("{ $or: [ { 'submitedBy' : ?0 }, { 'reviewers.reviewerId' : ?0 } ], 'submitStatus': { $in: ?1 } }")
  List<Assessment> findBySubmitStatusInAndSubmitedByOrReviewerId(String userId, List<AssessmentStatus> statuses);



    default List<Assessment> findByFilterCriteria(ReportFilters reportFilters) {
        StringBuilder queryBuilder = new StringBuilder("{");
        if (!CollectionUtils.isEmpty(reportFilters.getSubmittedBy())) {
            List<String> submittedByValues = reportFilters.getSubmittedBy();
            String submittedByQuery = submittedByValues.stream().map(value -> "'" + value + "',").collect(Collectors.joining("", "'submitedBy': { $in : [", "] },"));
            queryBuilder.append(submittedByQuery);
        }
        if (reportFilters.getAccountId() != null && reportFilters.getAccountId() != 0) {
            queryBuilder.append("'account.$id':").append(reportFilters.getAccountId()).append(",");
        }

        if (!CollectionUtils.isEmpty(reportFilters.getProjectIds())) {
            queryBuilder.append("'project.$id': { $in: ").append(reportFilters.getProjectIds()).append(" },");
        }

        if (!CollectionUtils.isEmpty(reportFilters.getTemplateId())) {
                    queryBuilder.append(" 'template.$id': { $in: ").append(reportFilters.getTemplateId()).append(" },");
        }

        if (reportFilters.getSubmissionFromDate() != null&& reportFilters.getSubmissionFromDate()>0 && reportFilters.getSubmissionToDate() != null&& reportFilters.getSubmissionToDate()>0) {
            queryBuilder.append("submitedAt:{");
            queryBuilder.append(" $gte: ").append(reportFilters.getSubmissionFromDate()).append(", ");
            queryBuilder.append("$lte: ").append(reportFilters.getSubmissionToDate());
            queryBuilder.append("},");

        }
        if (reportFilters.getScoreFromRange() != null&& reportFilters.getScoreFromRange()>=0 && reportFilters.getScoreToRange() != null&& reportFilters.getScoreToRange()>0) {
            queryBuilder.append("score:{");
            queryBuilder.append(" $gte: ").append(reportFilters.getScoreFromRange()).append(", ");
            queryBuilder.append("$lte: ").append(reportFilters.getScoreToRange());
            queryBuilder.append("},");

        }


        if (!CollectionUtils.isEmpty(reportFilters.getProjectType())) {
            queryBuilder.append("'projectType.$id': { $in:").append(reportFilters.getProjectType() ).append("},");
        }

        if (StringUtils.isNotBlank(reportFilters.getSubmitStatus())) {
            queryBuilder.append("'submitStatus': ").append(reportFilters.getSubmitStatus() ).append(",");
        }else {
            queryBuilder.append("'submitStatus':{ $nin:").append(List.of(AssessmentStatus.SAVE) ).append("},");
        }

        if (!CollectionUtils.isEmpty(reportFilters.getReviewedBy())) {
            List<String> reviewedBy = reportFilters.getReviewedBy();
            String reviewedByIds = reviewedBy.stream().map(value -> "'" + value + "',").collect(Collectors.joining("", "'reviewers.reviewerId': { $in : [", "] },"));
            queryBuilder.append(reviewedByIds);
        }

        if (queryBuilder.charAt(queryBuilder.length() - 1) == ',') {
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);}
        queryBuilder.append("}");

        return findByQuery(queryBuilder.toString());
    }

    @Query(value = "?0")
    List<Assessment> findByQuery(String query);

}
