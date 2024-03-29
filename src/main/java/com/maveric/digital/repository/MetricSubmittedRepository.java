package com.maveric.digital.repository;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.ConsulateAccountCountDto;
import com.maveric.digital.responsedto.ConsulateProjectCountDto;
import com.maveric.digital.responsedto.ReportFilters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.MetricSubmitted;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface MetricSubmittedRepository extends MongoRepository<MetricSubmitted, Long> {
    @Aggregation(pipeline = {"{'$match': {'submittedAt':{'$gte':?0,'$lte':?1,'$exists':true}, 'projectTypeId': ?2,'submitStatus': { $in: ?3 }}}",
            "{'$sort':{'submittedAt':1}}",
            "{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submittedAt'}}},'count': { '$count': {} }}}"})
    List<LineChartProjection> submitMetricLineChartStartAndEndDatesProjectTypeId(Long startDate, Long endDate, Long projectTypeId, List<AssessmentStatus> assessmentStatusList);

    @Aggregation(pipeline = {"{'$match': {'submittedAt':{'$gte':?0,'$lte':?1,'$exists':true},'project.$id': {$in: ?2},'submitStatus': { $in: ?3 } }}",
            "{'$sort':{'submittedAt':1}}",
            "{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submittedAt'}}},'count': { '$count': {} }}}"})
    List<LineChartProjection> submitMetricLineChartStartAndEndDatesAndProjectId(Long startDate, Long endDate,
                                                                                List<Long> projectId, List<AssessmentStatus> assessmentStatusList);

    @Aggregation(pipeline = {"{'$match': {'submittedAt':{'$gte':?0,'$lte':?1,'$exists':true},'accountId': ?2,'submitStatus': { $in: ?3 } }}",
            "{'$sort':{'submittedAt':1}}",
            "{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submittedAt'}}},'count': { '$count': {} }}}"})
    List<LineChartProjection> submitMetricLineChartStartAndEndDatesAndAccountId(Long startDate, Long endDate, Long accountId, List<AssessmentStatus> assessmentStatusList);

    @Aggregation(pipeline = {"{'$match': {'submittedAt':{'$gte':?0,'$lte':?1,'$exists':true},'submitStatus': { $in: ?2 } }}",
            "{'$sort':{'submittedAt':1}}",
            "{'$group':{'_id':{'$dateToString':{'format': '%d-%m-%Y','date':{$toDate:'$submittedAt'}}},'count': { '$count': {} }}}"})
    List<LineChartProjection> submitMetricLineChartStartandEndDates(Long startDate, Long endDate, List<AssessmentStatus> assessmentStatusList);

    Integer countBySubmitStatusIn(List<AssessmentStatus> statusList);

    @Query("{'submitStatus':'SUBMITTED'}")
    List<MetricSubmitted> findAllSubmittedMetricOrderByUpdatedAtDesc(Sort sort);

    List<MetricSubmitted> findAll();

    List<MetricSubmitted> findAllBySubmittedByOrderByUpdatedAtDesc(String submittedBy);

    List<MetricSubmitted> findAllBySubmittedByNotAndSubmitStatusOrderByUpdatedAtDesc(String submittedBy, String submitStatus, Sort sort);

    List<MetricSubmitted> findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus status, Sort sort);

    List<MetricSubmitted> findTop10BySubmitStatusInOrderByUpdatedAtDesc(List<AssessmentStatus> assessmentStatusList);

    @Query("{'businessUnit.id' : ?0}")
    List<MetricSubmitted> findByBusinessUnitId(Long id);

    @Query(value = "{'project.$id': {$in: ?0}}")
    List<MetricSubmitted> findByProjectIds(List<Long> ids);

    @Query("{'projectType.id' : ?0}")
    List<MetricSubmitted> findByProjectTypeId(Long id);

    @Query("{'account.id' : ?0}")
    List<MetricSubmitted> findByAccountId(Long id);

    List<MetricSubmitted> findBySubmitStatusIn(List<AssessmentStatus> status);

    List<MetricSubmitted> findTop10ByAccountIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue, List<AssessmentStatus> assessmentStatusList);

    @Aggregation(pipeline = {"{ '$match': { 'project.$id': { '$in': ?0 }, 'submitStatus': { '$in': ?1 } } }", "{ '$sort': { 'updatedAt': -1 } }", "{ '$limit': 10 }"})
    List<MetricSubmitted> findTop10ByProjectIdInAndSubmitStatusInOrderByUpdatedAtDesc(List<Long> filterValue, List<AssessmentStatus> assessmentStatusList);

    List<MetricSubmitted> findTop10ByProjectTypeIdAndSubmitStatusInOrderByUpdatedAtDesc(Long filterValue, List<AssessmentStatus> assessmentStatusList);

    List<MetricSubmitted> findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus assessmentStatus, String reviewerId);

    @Aggregation(pipeline = {
            "{'$match':{'submittedAt':{'$exists':true}}}",
            "{$group: {'_id': '$project', 'count': {$sum: 1}}}"
    })
    List<ConsulateProjectCountDto> countByProject();

    @Aggregation(pipeline = {
            "{'$match':{'submittedAt':{'$exists':true}}}",
            "{$group: {'_id': '$account', 'count': {$sum: 1}}}"
    })
    List<ConsulateAccountCountDto> countByAccount();

    @Query("{'submitedBy': ?0,'template.id': ?1, 'project.id': ?2}")
    @Update("{'$set': {'isFrequencyRequired': ?3}}")
    void findBySubmittedByAndTemplateIdAndProjectId(String submittedBy, Long templateId, Long projectId, Boolean flag);

    List<MetricSubmitted> findByFrequencyReminderDateBetweenAndIsFrequencyRequiredTrue(Long startDate, Long endDate);

    List<MetricSubmitted> findByFrequencyOverDueRemindersDateBetweenAndIsFrequencyRequiredTrue(Long startDate, Long endDate);

    @Query(value = "{'id' : ?0}")
    @Update(value = "{'$set': {'frequencyRemindersSent': ?1}}")
    void updateFrequencyRemindersSent(Long id, List<Long> frequencyRemindersSent);

    @Query("{'submittedBy': ?0,'template.id': ?1, 'project.id': ?2,'id': { $ne: ?4 }}")
    @Update("{'$set': {'isFrequencyRequired': ?3}}")
    void findBySubmittedByAndTemplateIdAndProjectIdAndUpdateisFrequencyRequired(String submittedBy, Long templateId, Long projectId, boolean flag, Long id);


    Integer countByAccountIdAndSubmitStatusIn(Long accountId, List<String> status);

    @Aggregation(pipeline = {
            "{$match: { 'project.$id': { $in: ?0 }, 'submitStatus': { $in: ?1 } }}",
            "{$group: { 'id': null, 'total': { $sum: 1 } }}"
    })
    Integer countByProjectIdInAndSubmitStatusIn(List<Long> projectIds, List<AssessmentStatus> statuses);

    @Query("{ $or: [ { 'submittedBy' : ?0 }, { 'reviewers.reviewerId' : ?0 } ], 'submitStatus': { $in: ?1 } }")
    List<MetricSubmitted> findBySubmitStatusInAndSubmitedByOrReviewerId(String userId, List<AssessmentStatus> statuses);

    default List<MetricSubmitted> findByFilterCriteria(ReportFilters reportFilters) {
        StringBuilder queryBuilder = new StringBuilder("{");
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(reportFilters.getSubmittedBy())) {
            List<String> submittedByValues = reportFilters.getSubmittedBy();
            String submittedByQuery = submittedByValues.stream().map(value -> "'" + value + "',").collect(Collectors.joining("", "'submittedBy': { $in : [", "] },"));
            queryBuilder.append(submittedByQuery);
        }
        if (reportFilters.getAccountId() != null && reportFilters.getAccountId() != 0) {
            queryBuilder.append("'account.$id':").append(reportFilters.getAccountId()).append(",");
        }

        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(reportFilters.getProjectIds())) {
            queryBuilder.append("'project.$id': { $in: ").append(reportFilters.getProjectIds()).append(" },");
        }

        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(reportFilters.getTemplateId())) {
            queryBuilder.append(" 'template.$id': { $in: ").append(reportFilters.getTemplateId()).append(" },");
        }

        if (reportFilters.getSubmissionFromDate() != null && reportFilters.getSubmissionFromDate() > 0 && reportFilters.getSubmissionToDate() != null && reportFilters.getSubmissionToDate() > 0) {
            queryBuilder.append("submittedAt:{");
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

        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(reportFilters.getProjectType())) {
            queryBuilder.append("'projectType.$id': { $in:").append(reportFilters.getProjectType()).append("},");
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
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }
        queryBuilder.append("}");

        return findByQuery(queryBuilder.toString());
    }

    @Query(value = "?0")
    List<MetricSubmitted> findByQuery(String query);

    @Query("{'account.id' : ?0,'submitStatus': { $in: ?1 }}")
    List<MetricSubmitted> findByAccountIdAndSubmitStatusIn(Long id, List<AssessmentStatus> status);

}

