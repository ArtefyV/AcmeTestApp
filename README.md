# ACME - Solutia.cz
**Demo aplikace ACME**  
Hlavním účelem této aplikace je demonstrovat implementaci některých funkčních změn ve zdrojovém kódu aplikace.

---

## Zadání
### Změna hesla (Nastavení)
**Cíl:**  
Umožnit uživateli změnit své heslo.

**Primární aktér:**  
Uživatel.

**Použité entity:**
- **User:** Informace o uživatelském účtu (jméno, e-mail, heslo).

**Kroky:**
1. Uživatel otevře modul Nastavení.
2. Systém předvyplní uživatelské jméno a e-mail z tabulky User.
3. Uživatel zadá nové heslo.
4. Systém ověří splnění bezpečnostních požadavků:
    - Délka min. 8 znaků.
    - Složitost hesla přes knihovnu Passay.
5. Systém zašifruje nové heslo a aktualizuje ho v tabulce User.
6. Systém zobrazí potvrzení o změně hesla.

**Alternativní tok:**
- Uživatel zadá nevalidní heslo.
- Systém zobrazí chybu, pokud heslo nesplňuje požadavky.
- Uživatel zadá nové heslo.

**Postpodmínky:**
- Heslo uživatele je úspěšně změněno v tabulce User.

---

## Implementace
### Úpravy backendu aplikace
- Do `SettingsController`u je přidán koncový bod `POST /settings/password`, ke kterému bude klient přistupovat za účelem změny hesla prostřednictvím UI modulu Nastavení.
- Příslušná metoda `SettingsController.changePassword()` přebírá jako parametry:
    - Stávající heslo.
    - Nové heslo k aktualizaci.
    - Duplikát nového hesla pro kontrolu podobnosti.  
      Další logika zpracování dat je předána metodě třídy služby `UserService`.
- Pro předání dat od klienta byla vytvořena třída `PasswordChangeDTO`, která obsahuje údaje pro změnu hesla.
- Do třídy služby `UserService` byla přidána metoda `changePassword()` pro předběžné ověření a uložení nového hesla.
- Do třídy `UserService` byla také přidána metoda `savePassword()`, která slouží k zašifrování a uložení hesla do úložiště. K šifrování se používá hashovací funkce `BCrypt`.
- Pro ověřování hesel byla vytvořena speciální třída `PasswordValidator`, která obsahuje metody `validate()` a `isValid()` sloužící ke kontrole hesla z hlediska splnění bezpečnostních požadavků. K tomuto účelu se používá knihovna `Passay`.
- V případě zadání hesla, které nevyhovuje bezpečnostním požadavkům, vrátí metoda `validate()` seznam chyb, který bude následně vrácen klientovi.

### Úpravy ve frontendové části aplikace
- Do formuláře pro aktualizaci hesla na stránce `settings.html` bylo přidáno pole pro nastavení stávajícího hesla.
    - Je to provedeno z bezpečnostních důvodů a je to nutné k vyloučení možnosti změny hesla neoprávněnou osobou, která dočasně získala přístup k počítači, na němž je otevřena stránka Nastavení aplikace.
- Do šablony `bootstrapLayout.html`, která se používá k vytvoření stránky Nastavení aplikace, byly přidány:
    - Skryté prvky dokumentu pro předávání hlášek.
    - Další funkce JavaScriptu, které realizují ukázku modálního okna s případnými hláškami.

---

## Změny v aplikaci nad rámec úkolu
Tato úloha nepopisuje případ požadavku na obnovení zapomenutého uživatelského hesla. Obvykle je však nedílnou součástí funkčnosti webové aplikace. Proto jsem tuto možnost implementoval dodatečně.

### Dodatečné úpravy backendu aplikace
- V kontroléru `UserManagementController` byla metoda `passwordResetAction()` koncového bodu `POST /password-reset` změněna tak, aby jako parametr přijímala e-mailovou adresu uživatele.
    - Tato metoda nejprve zkontroluje, zda v databázi existuje uživatel s příslušnou e-mailovou adresou, a poté vygeneruje dočasné heslo.
    - Pokud je server SMTP v nastavení aplikace správně nakonfigurován, odešle `JavaMailSender` uživateli zprávu s dočasným heslem.

### Dodatečné úpravy ve frontendové části aplikace
- Byla vytvořena stránka `password-reset.html`, na kterou byl umístěn formulář s jediným polem pro e-mailovou adresu, kam bude po kontrole platnosti odesláno dočasné heslo pro přihlášení do aplikace.
- Do šablony `emptyLayout.html`, která se používá k vytvoření stránek Přihlášení aplikace a k odeslání požadavku na obnovení hesla, byly přidány:
    - Skryté prvky dokumentu pro předávání hlášek.
    - Další funkce JavaScriptu, které realizují demonstraci modálního okna s případnými hláškami.

---

## Budoucí vylepšení
- **Dvoufaktorová autentizace (2FA):** Přidání podpory pro dvoufaktorovou autentizaci pro zvýšení bezpečnosti uživatelských účtů.
- **Historie hesel:** Implementace kontroly, aby uživatel nemohl znovu použít některé z předchozích hesel.
- **Notifikace o změně hesla:** Odesílání e-mailových notifikací uživatelům při změně hesla.
- **Zlepšení UI:** Přidání vizuální indikace síly hesla při jeho zadávání.

---

## Závěr
Tato aplikace demonstruje implementaci klíčových funkcí pro správu uživatelských hesel, včetně jejich změny a obnovení. Realizované změny zlepšují bezpečnost a uživatelskou přívětivost aplikace. Přestože byly splněny všechny požadavky zadání, existuje prostor pro další vylepšení, která mohou dále zvýšit bezpečnost a funkčnost systému. Tato dokumentace slouží jako základ pro budoucí rozvoj aplikace.