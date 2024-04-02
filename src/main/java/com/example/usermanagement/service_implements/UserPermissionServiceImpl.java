package com.example.usermanagement.service_implements;

import com.example.usermanagement.dto.Request.UpdatePermissionRequest;
import com.example.usermanagement.dto.Response.GetUserPermissionsResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.enums.Permission;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    @Override
    public List<GetUserPermissionsResponse> getUserPermission() {
        var listUserDTO = userPermissionRepository.findAll(); // fake
        List<GetUserPermissionsResponse> responseList = new ArrayList<>();
        for (UserPermission userPermission : listUserDTO) {
            String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
            GetUserPermissionsResponse responseObj = new GetUserPermissionsResponse();
            Class<?> objClass = userPermission.getClass();
            Field[] fields = objClass.getDeclaredFields();
            int countField = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                if (countField == 7) {
                    break;
                }
                try {
                    Object fieldValue = field.get(userPermission);
                    if (countField != 0) {
                        String permissionString;
                        if (countField != 1) {
                            permissionString =
                                    fieldValue.toString().substring(1, fieldValue.toString().length() - 1);
                        } else {
                            permissionString = fieldValue.toString();
                        }
                        switch (countField) {
                            case 1:
                                responseObj.setRoleName(fieldValue.toString());
                                break;
                            case 2:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setSyllabus(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setSyllabus(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setSyllabus(permissionString);
                                    }
                                } else {
                                    responseObj.setSyllabus(permissionArr[1]);
                                }
                                break;
                            case 3:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setTraining(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setTraining(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setTraining(permissionString);
                                    }
                                } else {
                                    responseObj.setTraining(permissionArr[1]);
                                }
                                break;
                            case 4:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserclass(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserclass(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserclass(permissionString);
                                    }
                                } else {
                                    responseObj.setUserclass(permissionArr[1]);
                                }
                                break;
                            case 5:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setLearningMaterial(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setLearningMaterial(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setLearningMaterial(permissionString);
                                    }
                                } else {
                                    responseObj.setLearningMaterial(permissionArr[1]);
                                }
                                break;
                            case 6:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserManagement(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserManagement(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserManagement(permissionString);
                                    }
                                } else {
                                    responseObj.setUserManagement(permissionArr[1]);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    countField++;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            responseList.add(responseObj);
        }
        return responseList;
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> updatePermission(List<UpdatePermissionRequest> updateRequest) {
        var permissionList = userPermissionRepository.findAll();
        final String[] prefixArr = {"ROLE", "SYLLABUS", "TRAINING", "CLASS", "MATERIAL", "USER"};
        String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
        for (UpdatePermissionRequest updatePermission : updateRequest) {
            for (UserPermission permission : permissionList) {
                if (permission.getRole().name().equals(updatePermission.getRoleName())) {
                    int nthChild = 0;
                    Class<?> objClass = updatePermission.getClass();
                    Field[] fields = objClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        List<Permission> tmpUserPermission = new ArrayList<>();
                        if (nthChild > 0 && nthChild < fields.length) {
                            try {
                                Object value = field.get(updatePermission);
                                if (value.equals(permissionArr[0])) {
                                    tmpUserPermission =
                                            List.of(
                                                    Permission.valueOf(prefixArr[nthChild] + "_DELETE"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_CREATE"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_MODIFY"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_VIEW"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_IMPORT"));
                                } else if (value.equals(permissionArr[1])) {
                                } else {
                                    tmpUserPermission = List.of(
                                            Permission.valueOf(prefixArr[nthChild] + "_" + value)
                                    );
                                }
                                switch (nthChild) {
                                    case 1:
                                        permission.setSyllabus(tmpUserPermission);
                                        break;
                                    case 2:
                                        permission.setTrainingProgram(tmpUserPermission);
                                        break;
                                    case 3:
                                        permission.setUserClass(tmpUserPermission);
                                        break;
                                    case 4:
                                        permission.setLearningMaterial(tmpUserPermission);
                                        break;
                                    case 5:
                                        permission.setUserManagement(tmpUserPermission);
                                        break;
                                    default:
                                        break;
                                }
                                nthChild++;
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            nthChild++;
                        }
                    }
                }
            }
        }
        var savedPermissionList = userPermissionRepository.saveAll(permissionList);
        ResponseObject responseObject = ResponseObject.builder()
                .status("Success")
                .message("User permission updated")
                .payload(
                        convertToResponseList(savedPermissionList)
                )
                .build();
        return ResponseEntity.ok(responseObject);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    private List<GetUserPermissionsResponse> convertToResponseList(List<UserPermission> userPermissionList) {
        List<GetUserPermissionsResponse> responseList = new ArrayList<>();
        for (UserPermission userPermission : userPermissionList) {
            String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
            GetUserPermissionsResponse responseObj = new GetUserPermissionsResponse();
            Class<?> objClass = userPermission.getClass();
            Field[] fields = objClass.getDeclaredFields();
            int countField = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                if (countField == 7) {
                    break;
                }
                try {
                    Object fieldValue = field.get(userPermission);
                    if (countField != 0) {
                        String permissionString;
                        if (countField != 1) {
                            permissionString =
                                    fieldValue.toString().substring(1, fieldValue.toString().length() - 1);
                        } else {
                            permissionString = fieldValue.toString();
                        }
                        switch (countField) {
                            case 1:
                                responseObj.setRoleName(fieldValue.toString());
                                break;
                            case 2:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setSyllabus(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setSyllabus(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setSyllabus(permissionString);
                                    }
                                } else {
                                    responseObj.setSyllabus(permissionArr[1]);
                                }
                                break;
                            case 3:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setTraining(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setTraining(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setTraining(permissionString);
                                    }
                                } else {
                                    responseObj.setTraining(permissionArr[1]);
                                }
                                break;
                            case 4:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserclass(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserclass(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserclass(permissionString);
                                    }
                                } else {
                                    responseObj.setUserclass(permissionArr[1]);
                                }
                                break;
                            case 5:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setLearningMaterial(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setLearningMaterial(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setLearningMaterial(permissionString);
                                    }
                                } else {
                                    responseObj.setLearningMaterial(permissionArr[1]);
                                }
                                break;
                            case 6:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserManagement(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserManagement(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserManagement(permissionString);
                                    }
                                } else {
                                    responseObj.setUserManagement(permissionArr[1]);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    countField++;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            responseList.add(responseObj);
        }
        return responseList;
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    private List<UserPermission> convertToObjectList(Object[] objects) {
        List<UserPermission> userPermissions = new ArrayList<>();
        for (Object obj : objects) {
            if (obj instanceof UserPermission) {
                userPermissions.add((UserPermission) obj);
            }
            // You might need additional logic if the conversion is more complex
        }
        return userPermissions;
    }
}
