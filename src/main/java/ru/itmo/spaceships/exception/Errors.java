package ru.itmo.spaceships.exception;

/**
 * Класс, описывающий все ошибки, возникающие в приложении.
 */
public class Errors {
    /**
     * Неожиданная неотловленная ошибка.
     */
    public static BaseException unhandledExceptionError() {
        return new BaseException(500, "internal.error", "Что-то пошло не так...");
    }

    /**
     * Ремонтник не найден.
     */
    public static BaseException repairmanNotFound(long id) {
        return new BaseException(404, "repairman.absent.error",
                String.format("Ремонтник с id=\"%s\" не найден", id));
    }

    /**
     * Корабль не найден.
     */
    public static BaseException spaceshipNotFound(long serial) {
        return new BaseException(404, "spaceship.absent.error",
                String.format("Корабль с серийным номером=\"%s\" не найден", serial));
    }

    /**
     * Заявка на обслуживание не найдена.
     */
    public static BaseException maintenanceRequestNotFound(long id) {
        return new BaseException(404, "maintenance.request.absent.error",
                String.format("Заявка на обслуживание с id=\"%s\" не найдена", id));
    }

    /**
     * Ошибка валидации: имя и должность обязательны для создания ремонтника.
     */
    public static BaseException repairmanValidationError() {
        return new BaseException(400, "repairman.validation.error",
                "Имя и должность обязательны для создания ремонтника");
    }

    /**
     * Ошибка валидации: серийный номер обязателен для создания корабля.
     */
    public static BaseException spaceshipSerialRequiredError() {
        return new BaseException(400, "spaceship.serial.required.error",
                "Серийный номер обязателен для создания корабля");
    }

    /**
     * Ошибка валидации: обязательные поля для создания корабля.
     */
    public static BaseException spaceshipRequiredFieldsError() {
        return new BaseException(400, "spaceship.required.fields.error",
                "Производитель, название, дата производства и тип обязательны для создания корабля");
    }

    /**
     * Ошибка валидации: серийный номер корабля обязателен для создания заявки на обслуживание.
     */
    public static BaseException maintenanceRequestSpaceshipSerialRequiredError() {
        return new BaseException(400, "maintenance.request.spaceship.serial.required.error",
                "Серийный номер корабля обязателен для создания заявки на обслуживание");
    }

    /**
     * Ошибка валидации: комментарий обязателен для создания заявки на обслуживание.
     */
    public static BaseException maintenanceRequestCommentRequiredError() {
        return new BaseException(400, "maintenance.request.comment.required.error",
                "Комментарий обязателен для создания заявки на обслуживание");
    }

    /**
     * Ошибка: недопустимый переход статуса заявки на обслуживание.
     */
    public static BaseException maintenanceRequestStatusTransitionError(String currentStatus, String newStatus) {
        return new BaseException(400, "maintenance.request.status.transition.error",
                String.format("Переход статуса из \"%s\" в \"%s\" не разрешён", currentStatus, newStatus));
    }

    /**
     * Ошибка валидации (общая).
     */
    public static BaseException validationError(String message) {
        return new BaseException(400, "validation.error", message != null ? message : "Ошибка валидации");
    }

    /**
     * Ошибка: невалидный JSON в теле запроса.
     */
    public static BaseException invalidJsonError() {
        return new BaseException(400, "invalid.json.error", "Невалидный JSON в теле запроса");
    }

    /**
     * Ошибка: невалидное тело запроса.
     */
    public static BaseException invalidRequestBodyError(String message) {
        return new BaseException(400, "invalid.request.body.error",
                message != null ? message : "Невалидное тело запроса");
    }

    /**
     * Ошибка: неверный тип параметра.
     */
    public static BaseException invalidParameterTypeError(String parameterName, String expectedType) {
        return new BaseException(400, "invalid.parameter.type.error",
                String.format("Неверный тип параметра '%s': ожидается %s", parameterName, expectedType));
    }

    /**
     * Ошибка: ошибка обработки входных данных.
     */
    public static BaseException invalidInputError(String message) {
        return new BaseException(400, "invalid.input.error",
                message != null ? message : "Ошибка обработки входных данных");
    }

    /**
     * Ошибка: статусный ответ Spring.
     */
    public static BaseException responseStatusError(int code, String message) {
        return new BaseException(code, "response.status.error",
                message != null ? message : "Ошибка обработки запроса");
    }
}

