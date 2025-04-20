def has_field(data: dict, field: str) -> bool:
    return data.get(field, None) is not None
