import requests
import pandas as pd

todas_Atvdds = {} #dicionario inteligente

count_dados_lidos = 0

with open('Atvdds_urls.txt', 'r', encoding = 'utf-8') as urls: # utf-8 é pra não bugar os acentos em pt
    for linha in urls:
        url_limpa = linha.strip() #lida com os /n
        if url_limpa: #verifica se a linha n  ta vazia
            pingada = requests.get(url_limpa)

            #Verificação ---------------------------------------------
            if pingada.status_code == 200:
                raw_data = pingada.json()

                if 'atividade' in raw_data and raw_data['atividade']:
                    for item in raw_data['atividade']:
                        
                        id_ativ = item.get('id') #Id vem do site do sesc, não é nosso id proprio

                        nome_atvdd = item.get('titulo','Sem título') #no get da pra fazer com case caso esteja vazio
                        complemento_atvdd = item.get('complemento',' ')

                        #Data e hora ------
                        raw_date_FS = item.get('dataPrimeiraSessao','Data não especificada')#Data da primeira sessão
                        raw_date_LS = item.get('dataUltimaSessao','')
                        raw_date_PS = item.get('dataProxSessao','')
                        
                        
                        clean_date_FS = raw_date_FS.replace('T',' ') if raw_date_FS else ' ' 
                        clean_date_LS = raw_date_LS.replace('T',' ') if raw_date_LS else ' '
                        clean_date_PS = raw_date_PS.replace('T',' ') if raw_date_PS else ' '
                        #------------------
                        
                        lista_unidades = [i.get('name') for i in item.get('unidade', []) if i.get('name')]
                        unidade_avdd = ", ".join(lista_unidades)

                        lista_categorias = [j.get('titulo') for j in item.get('categorias', []) if j.get('titulo')]
                        categoria_atvdd = ", ".join(lista_categorias)

                        campo_gratuito = item.get('gratuito', '').strip()
                        if campo_gratuito == "": #quando o campo gratuito ta vazio a atvdd é paga
                            pagamento = "Pago" 
                        else:
                            pagamento = "Gratuito"

                        link = "https://www.sescsp.org.br" + item.get('link', '')#O link no ngc não ta completo

                        
                        todas_Atvdds[id_ativ] = {
                        "Nome da Atividade": nome_atvdd,
                        "Data Proxima seção": clean_date_PS,
                        "Data Primeira seção": clean_date_FS,
                        "Data Ultima seção": clean_date_LS,
                        "Unidade": unidade_avdd,
                        "Categorias": categoria_atvdd,
                        "Acesso": pagamento, 
                        "Link": link}

                count_dados_lidos += 1
            else:
                print(f"Erro. Código de erro: ", pingada.status_code)

print(f"Dados extraídos com sucesso:", count_dados_lidos) #deve estar igual ao numero de links inseridos no txt de URLS          
#----------------------------------------------------
#Output File (TXT)
with open('Atividades_SESC.txt', 'w', encoding='utf-8') as Atvdds:
    
    # Passa por cada atividade guardada no dicionário
    for atividade in todas_Atvdds.values():
        
        # Escreve cada informação em uma linha diferente
        Atvdds.write(f"Nome da Atividade: {atividade['Nome da Atividade']}\n")
        Atvdds.write(f"Data Próxima sessão: {atividade['Data Proxima seção']}\n")
        Atvdds.write(f"Data Primeira sessão: {atividade['Data Primeira seção']}\n")
        Atvdds.write(f"Data Última sessão: {atividade['Data Ultima seção']}\n")
        Atvdds.write(f"Unidade: {atividade['Unidade']}\n")
        Atvdds.write(f"Categorias: {atividade['Categorias']}\n")
        Atvdds.write(f"Acesso: {atividade['Acesso']}\n")
        Atvdds.write(f"Link: {atividade['Link']}\n")
        
        # Adiciona uma linha divisória para separar da próxima atividade
        Atvdds.write("-" * 50 + "\n\n")


#Output File (CSV)

lista_de_atividades = list(todas_Atvdds.values())
df_atividades = pd.DataFrame(lista_de_atividades) #transforma no dataframe
df_atividades.to_csv("Atividades_SESC.csv", index=False, sep=";", encoding="utf-8-sig") #salva em CSV