package com.ecommerce.api.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ecommerce.api.exception.InvalidRequestParameterException;

public class StrictPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        String page = webRequest.getParameter(
                getParameterNameToUse(getPageParameterName(), methodParameter));

        String pageSize = webRequest.getParameter(
                getParameterNameToUse(getSizeParameterName(), methodParameter));

        validatePage(page);
        validatePageSize(pageSize);

        return super.resolveArgument(
                methodParameter,
                mavContainer,
                webRequest,
                binderFactory);
    }

    private void validatePage(String page) {

        if (page == null) {
            return;
        }

        try {
            int pageNumber = Integer.parseInt(page);

            if (pageNumber < 0) {
                throw new InvalidRequestParameterException(
                        "Page must be greater than or equal to 0");
            }

        } catch (NumberFormatException exception) {
            throw new InvalidRequestParameterException(
                    "Page must be a valid integer");
        }
    }

    private void validatePageSize(String pageSize) {

        if (pageSize == null) {
            return;
        }

        try {
            int size = Integer.parseInt(pageSize);

            if (size < 1 || size > 50) {
                throw new InvalidRequestParameterException(
                        "Page size must be between 1 and 50");
            }

        } catch (NumberFormatException exception) {
            throw new InvalidRequestParameterException(
                    "Page size must be a valid integer");
        }
    }

}
