package com.smanager.services;

import com.smanager.Cache;
import com.smanager.controllers.AssignmentController;
import com.smanager.controllers.SolutionController;
import com.smanager.dao.models.User;
import com.smanager.utils.WorkControllerPaths;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {

    private static final String SUFFIX = "#";

    public String getSolutionRedirectWorkUrl(User user) {
        String redirectWorkUrl;

        if (user != null && user.isStudent()) {
            redirectWorkUrl = WorkControllerPaths.getRedirectPath(WorkControllerPaths.INDEX);
        } else if (user != null && user.isTeacher()) {
            redirectWorkUrl = WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK);
        } else {
            redirectWorkUrl = SolutionController.INDEX_REDIRECT_STRING;
        }

        return redirectWorkUrl;
    }

    public String getSolutionRedirectWorkUrlAndScrollToSelectedId(User user, String id) {
        if (id == null) {
            return getSolutionRedirectWorkUrl(user);
        }
        Cache.putDiv(user.getId(), id);
        return getSolutionRedirectWorkUrl(user) + SUFFIX + id;
    }

    public String getAssignmentRedirectWorkUrl(User user) {
        String redirectWorkUrl;

        if (user != null && user.isStudent()) {
            redirectWorkUrl = WorkControllerPaths.getRedirectPath(WorkControllerPaths.INDEX);
        } else if (user != null && user.isTeacher()) {
            redirectWorkUrl = WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK);
        } else {
            redirectWorkUrl = AssignmentController.INDEX_REDIRECT_STRING;
        }

        return redirectWorkUrl;
    }

    public String getAssignmentRedirectWorkUrlAndScrollToSelectedId(User user, String id) {
        if (id == null) {
            return getAssignmentRedirectWorkUrl(user);
        }
        Cache.putDiv(user.getId(), id);
        return getAssignmentRedirectWorkUrl(user) + SUFFIX + id;
    }
}
