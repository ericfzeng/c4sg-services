package org.c4sg.dao;

import org.c4sg.entity.JobTitle;
import org.c4sg.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.transaction.Transactional;

public interface ProjectDAO extends CrudRepository<Project, Long> {

    String FIND_BY_USER_ID_AND_STATUS = 
    		"SELECT p FROM UserProject up " +
            "JOIN up.project p " +
            "WHERE up.user.id = :userId" +
            " AND (:userProjectStatus is null OR up.status = :userProjectStatus)" +
            "ORDER BY up.createdTime DESC";

    String FIND_BY_ORGANIZATION_ID_AND_STATUS = 
    		"SELECT p FROM Project p " +
    		"WHERE p.organization.id = :orgId" +
            " AND (:projectStatus is null OR p.status = :projectStatus)" +
            "ORDER BY p.createdTime DESC";

    String FIND_BY_NAME_OR_DESCRIPTION = 
    		"SELECT p FROM Project p " +
            "WHERE p.name LIKE CONCAT('%', :name, '%') " +
            "OR p.description LIKE CONCAT('%', :description, '%') " +
            "ORDER BY p.createdTime DESC";

    String FIND_BY_KEYWORD_SKILL_CRITERIA = 
    		"SELECT DISTINCT p "
    		+ "FROM ProjectSkill ps "
    		+ "RIGHT OUTER JOIN ps.project p "
    		+ "LEFT OUTER JOIN ps.skill s "
    		+ "LEFT OUTER JOIN p.organization o "
            + "WHERE ("
            + 	"(:keyWord is null OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))" 
            +   " OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +	" OR LOWER(p.state) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(p.country) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +	" OR LOWER(o.state) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.country) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(s.skillName) LIKE LOWER(CONCAT('%',:keyWord,'%')))"
            +   " AND (ps.skill.id in (:skills))"
            //+   " AND (:skillCount = (select count(distinct ps2.skill.id) from ProjectSkill ps2 where ps2.project.id=ps.project.id and ps2.skill.id in (:skills)) OR :skillCount=0)" 
            +   " AND (:status is null OR p.status = :status)"
            +   " AND (:jobTitleId is null OR p.jobTitleId = :jobTitleId)"
            +   " AND (:remote is null OR p.remoteFlag = :remote)"
            +   ")  "
            + "ORDER BY p.createdTime DESC";
    
    String FIND_BY_KEYWORD_CRITERIA = 
    		"SELECT DISTINCT p "
    		+ "FROM ProjectSkill ps "
    		+ "RIGHT OUTER JOIN ps.project p "
    		+ "LEFT OUTER JOIN ps.skill s "
    		+ "LEFT OUTER JOIN p.organization o "
            + "WHERE ("
            + 	"(:keyWord is null OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))" 
            +   " OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +	" OR LOWER(p.state) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(p.country) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +	" OR LOWER(o.state) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(o.country) LIKE LOWER(CONCAT('%', :keyWord, '%'))"
            +   " OR LOWER(s.skillName) LIKE LOWER(CONCAT('%',:keyWord,'%')))"            
            //+   " AND (:skillCount = (select count(distinct ps2.skill.id) from ProjectSkill ps2 where ps2.project.id=ps.project.id and ps2.skill.id in (:skills)) OR :skillCount=0)" 
            +   " AND (:status is null OR p.status = :status)"
            +   " AND (:jobTitleId is null OR p.jobTitleId = :jobTitleId)"
            +   " AND (:remote is null OR p.remoteFlag = :remote)"
            +   ")  "
            + "ORDER BY p.createdTime DESC";
    
    String DELETE_PROJECT = "UPDATE Project p set p.status = 'C' where p.id = :projId";

  	String SAVE_IMAGE = "UPDATE Project p set p.imageUrl = :imgUrl where p.id = :projectId";
  	
    String FIND_JOB_TITLES = "SELECT j FROM JobTitle j order by j.displayOrder";
  
	Project findById(int id);
	Project findByName(String name);

    List<Project> findAllByOrderByIdDesc();
	Project findByNameAndOrganizationId(String name, Integer orgId);
	
    @Query(FIND_BY_NAME_OR_DESCRIPTION)
    List<Project> findByNameOrDescription(@Param("name") String name, @Param("description") String description);

    @Query(FIND_BY_KEYWORD_SKILL_CRITERIA)
    Page<Project> findByKeywordAndSkill(@Param("keyWord") String keyWord, @Param("jobTitleId") Integer jobTitleId, @Param("skills") List<Integer> skills, @Param("status") String status, @Param("remote") String remote, Pageable pageable);
    
    @Query(FIND_BY_KEYWORD_CRITERIA)
    Page<Project> findByKeyword(@Param("keyWord") String keyWord, @Param("jobTitleId") Integer jobTitleId, @Param("status") String status, @Param("remote") String remote, Pageable pageable);

    @Query(FIND_BY_KEYWORD_SKILL_CRITERIA)
    List<Project> findByKeywordAndSkill(@Param("keyWord") String keyWord, @Param("jobTitleId") Integer jobTitleId, @Param("skills") List<Integer> skills, @Param("status") String status, @Param("remote") String remote);
    
    @Query(FIND_BY_KEYWORD_CRITERIA)
    List<Project> findByKeyword(@Param("keyWord") String keyWord, @Param("jobTitleId") Integer jobTitleId, @Param("status") String status, @Param("remote") String remote);
    
	@Query(FIND_BY_ORGANIZATION_ID_AND_STATUS)
	List<Project> getProjectsByOrganization(@Param("orgId") Integer orgId, @Param("projectStatus") String projectStatus);
	
	@Query(FIND_BY_USER_ID_AND_STATUS)
	List<Project> findByUserIdAndUserProjectStatus(@Param("userId") Integer userId, @Param("userProjectStatus") String userProjectStatus);
	
	@Transactional
    @Modifying
	@Query(DELETE_PROJECT)
	Integer deleteProject(@Param("projId") int projId);
	
    @Transactional
    @Modifying
    @Query(SAVE_IMAGE)
    void updateImage(@Param("imgUrl") String imgUrl, @Param("projectId") Integer projectId);
   
    @Query(FIND_JOB_TITLES)
    List<JobTitle> findJobTitles();
}
