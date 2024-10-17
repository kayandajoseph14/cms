package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    // Autowired dependency for the CategoryDao (Data Access Object)
    @Autowired
    CategoryDao categoryDao;

    // Autowired dependency for JwtFilter to check if the user is an admin
    @Autowired
    JwtFilter jwtFilter;

    // This method handles the addition of a new category
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            // Check if the user is an admin
            if (jwtFilter.isAdmin()) {
                // Validate the incoming request (check if required data like 'name' is present)
                if (validateCategoryMap(requestMap, false)) {
                    // Save the category into the database
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Category successfully added", HttpStatus.OK);
                }
            } else {
                // Return unauthorized access if the user is not an admin
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // If something goes wrong, return a generic error message
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // This method validates the request map, checking if it contains required keys like 'name' and optionally 'id'
    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            // If validateId is true, ensure that 'id' is also present in the request
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                // If we are adding a new category, no need to validate 'id'
                return true;
            }
        }
        return false;  // If validation fails, return false
    }

    // This method creates a Category object from the request map
    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd) {
        Category category = new Category();

        // If the operation is 'Add', set the id from the request map
        if (isAdd) {
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        // Set the name of the category
        category.setName(requestMap.get("name"));

        return category;
    }

    // This method fetches all categories from the database
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {

        try {
            // If filterValue is provided and equals "true", fetch filtered categories
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Inside if");
                return new ResponseEntity<>(categoryDao.getAllCategory(), HttpStatus.OK);
            }

            // If no filter is provided, return all categories
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // If something goes wrong, return an empty list and internal server error
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // This method handles updating an existing category
    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            // Check if the user is an admin
            if (jwtFilter.isAdmin()) {
                // Validate the request data, checking both 'id' and 'name'
                if (validateCategoryMap(requestMap, true)) {
                    // Find the category by 'id' in the database
                    Optional<Category> optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));

                    // If the category exists, update it
                    if (!optional.isEmpty()) {
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Category ID does not exist", HttpStatus.OK);
                    }
                }

                // If validation fails, return bad request
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            } else {
                // If the user is not an admin, return unauthorized access
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // If something goes wrong, return a generic error message
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
