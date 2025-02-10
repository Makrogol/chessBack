
def convert_cyclechess_fen_to_normal_fen(fen: str) -> str:
    normal_fen = ""
    for k in range(len(fen)):
        if fen[k] == ' ':
            normal_fen += fen[k:]
            break

        if fen[k].isalpha():
            normal_fen += fen[k]
            k += 1
    return normal_fen
