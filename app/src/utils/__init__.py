class GLobalUtils:
     @staticmethod
     def make_first_letter_to_capital(field: str):
          full_name = field.strip().lower().split(" ")
          formatted_full_name = ""
          for name in full_name:
               formatted_full_name += name[0].upper() + "" + name[1:] + " "

          return formatted_full_name